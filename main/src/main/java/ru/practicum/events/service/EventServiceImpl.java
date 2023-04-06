package ru.practicum.events.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatisticClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.events.dto.*;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.*;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapper.ParticipationRequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.ParticipationRequestStatus;
import ru.practicum.requests.repository.ParticipationRequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ParticipationRequestMapper participationRequestMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final EntityManager entityManager;
    private final StatisticClient statisticClient;

    @Transactional
    @Override
    public EventFullDto addEvent(NewEventDto eventDto, Long userId) {
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Дата и время на которые намечено событие не может быть раньше," +
                    " чем через два часа от текущего момента");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Category category = categoryRepository.findById(eventDto.getCategoryId()).orElseThrow(
                () -> new NotFoundException("Категория не найдена"));
        Event event = eventMapper.fromDto(eventDto, user, category);
        event.setState(EventState.PENDING);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsOfUser(Long userId, Integer from, Integer size) {
        QEvent qEvent = QEvent.event;
        JPAQueryFactory factory = new JPAQueryFactory(entityManager);

        List<Event> events = factory
                .selectFrom(qEvent)
                .where(qEvent.initiator.id.eq(userId))
                .offset(from)
                .limit(size)
                .setHint("javax.persistence.fetchgraph", entityManager.getEntityGraph("event-entity-graph"))
                .fetch();

        return mapToEventShortDtos(events, false);
    }

    @Override
    public EventFullDto getFullEventOfUser(Long userId, Long eventId) {
        Event event = getEventWithGraphAndValidate(userId, eventId);
        Long confirmedRequests = event.getRequests().stream()
                .filter(participationRequest -> Objects.equals(participationRequest.getStatus(),
                        ParticipationRequestStatus.CONFIRMED))
                .count();
        Long views = getViewsOfOneEvent(eventId);
        return eventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByUser(UpdateEventUserRequest eventRequest, Long userId, Long eventId) {
        Event updateEvent = eventRepository.findById(eventId).orElseThrow(() -> new ConflictException("Событие не найдено"));
        validateEventDto(eventRequest, userId, updateEvent);
        changeCategory(eventRequest, updateEvent);
        changeStateAction(eventRequest, updateEvent);
        changeCommonFields(eventRequest, updateEvent);
        eventRepository.save(updateEvent);
        return eventMapper.toEventFullDto(updateEvent);
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        Event event = getEventWithGraphAndValidate(userId, eventId);
        List<ParticipationRequest> requests = event.getRequests();
        return participationRequestMapper.toDtos(requests);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return null;
        }
        Collection<ParticipationRequest> requests = event.getRequests();
        Collection<Long> changingRequestsIds = request.getRequestsIds();
        List<ParticipationRequest> changingRequests = requests.stream()
                .filter(request1 -> changingRequestsIds.contains(request1.getId()))
                .collect(Collectors.toList());
        validateRequests(changingRequests);

        switch (request.getStatus()) {
            case CONFIRMED:
                confirmRequests(changingRequests, event);
                break;
            case REJECTED:
                changingRequests = changingRequests.stream()
                        .peek(request1 -> request1.setStatus(ParticipationRequestStatus.REJECTED))
                        .collect(Collectors.toList());
                break;
            default:
                throw new ValidationException("Статус должен быть CONFIRMED или REJECTED");
        }
        participationRequestRepository.saveAll(changingRequests);
        Map<Boolean, List<ParticipationRequestDto>> requestDtos =
                changingRequests.stream()
                        .map(participationRequestMapper::toDto)
                        .collect(Collectors.partitioningBy(
                                request2 -> Objects.equals(request2.getStatus(), ParticipationRequestStatus.CONFIRMED)));

        return new EventRequestStatusUpdateResult(requestDtos.get(true), requestDtos.get(false));
    }

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Integer from, Integer size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> root = cq.from(Event.class);
        cq.select(root);

        // создаем предикаты для where
        List<Predicate> predicates = new ArrayList<>();
        if (users != null) {
            predicates.add(root.get("initiator").in(users));
        }
        if (categories != null) {
            predicates.add(root.get("category").in(categories));
        }
        if (states != null) {
            predicates.add(root.get("state").in(states));
        }

        if (rangeStart != null && rangeEnd != null) {
            predicates.add(cb.between(root.get("eventDate"), rangeStart, rangeEnd));
        } else if (rangeStart != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        } else if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Event> query = entityManager.createQuery(cq);
        query.setFirstResult(from).setMaxResults(size);

        // просим добавить к запросу сущности User + Category + List PartRequests (тогда у нас будет 1 запрос вместо 4)
        EntityGraph<?> entityGraph = entityManager.getEntityGraph("event-entity-graph");
        query.setHint("javax.persistence.fetchgraph", entityGraph);

        List<Event> events = query.getResultList();

        List<EventFullDto> eventDtos = new ArrayList<>();

        // запрашиваем просмотры в сервисе статистики (будут получены только те события, у которых были просмотры)
        Map<Long, Long> viewsMap = getViewsMap(events);

        for (Event event : events) {
            long views = viewsMap.getOrDefault(event.getId(), 0L);
            long participantLimit = event.getParticipantLimit();
            long confirmedRequests = getConfirmedRequests(event, participantLimit);
            eventDtos.add(eventMapper.toEventFullDto(event, confirmedRequests, views));
        }
        return eventDtos;
    }

    @Override
    public List<EventShortDto> getEvents(String text, Boolean paid, Boolean onlyAvailable, EventSort sort,
                                         List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Integer from, Integer size, String ip) {
        JPAQueryFactory factory = new JPAQueryFactory(entityManager);
        QEvent qEvent = QEvent.event;
        JPAQuery<Event> query = factory.selectFrom(qEvent)
                .where(qEvent.state.eq(EventState.PUBLISHED));
        addDates(rangeStart, rangeEnd, query, qEvent);
        addTextCategoriesAndPaid(text, categories, paid, query, qEvent);
        if (sort != null && sort.equals(EventSort.EVENT_DATE)) {
            query.orderBy(qEvent.eventDate.asc());
        }
        query.offset(from).limit(size);

        EntityGraph<?> entityGraph = entityManager.getEntityGraph("event-entity-graph");
        query.setHint("javax.persistence.fetchgraph", entityGraph);

        List<Event> events = query.fetch();

        List<EventShortDto> eventShortDtos = mapToEventShortDtos(events, onlyAvailable);

        if (sort != null && sort.equals(EventSort.VIEWS)) {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }
        statisticClient.postStat("ewm-main-service", "/events", ip, LocalDateTime.now());
        return null;
    }

    @Override
    public EventFullDto getFullEvent(Long eventId, String ip) {
        EntityGraph<?> entityGraph = entityManager.getEntityGraph("event-entity-graph");
        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.fetchgraph", entityGraph);
        Event event = entityManager.find(Event.class, eventId, properties);
        validateEvent(event);
        Long confirmedRequests = event.getRequests().stream()
                .filter(request -> Objects.equals(request.getStatus(), ParticipationRequestStatus.CONFIRMED))
                .count();
        // получаем количество просмотров события (из статистики)
        Long views = getViewsOfOneEvent(eventId);
        // отправляем в статистику информацию о просмотре события
        statisticClient.postStatMonolith("ewm-main-service", "/events/" + eventId, ip, LocalDateTime.now());
        return eventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Transactional
    @Override
    public EventFullDto changeEventByAdmin(Long eventId, UpdateEventAdminRequest eventAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие не найдено"));
        changeEventDate(eventAdminRequest, event);
        changeStateAction(eventAdminRequest, event);
        changeCategory(eventAdminRequest, event);
        changeCommonFields(eventAdminRequest, event);
        Event changedEvent = eventRepository.save(event);
        return eventMapper.toEventFullDto(changedEvent);
    }

    private void changeStateAction(UpdateEventAdminRequest eventDto, Event event) {
        StateAdminAction stateAction = eventDto.getStateAction();
        if (stateAction == null) {
            return;
        }
        switch (stateAction) {
            case PUBLISH_EVENT:
                if (eventDto.getEventDate().isBefore(LocalDateTime.now().plus(1, ChronoUnit.HOURS))) {
                    throw new ValidationException(
                            "Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
                }
                if (Objects.equals(event.getState(), EventState.PENDING)) {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                } else {
                    throw new ConflictException("" +
                            "Событие можно публиковать, только если оно в состоянии ожидания публикации");
                }
                break;
            case REJECT_EVENT:
                if (!Objects.equals(event.getState(), EventState.PUBLISHED)) {
                    event.setState(EventState.CANCELED);
                } else {
                    throw new ConflictException("" +
                            "Событие можно отклонить, только если оно еще не опубликовано");
                }
                break;
            default:
                throw new ValidationException("Состояние изменяемого события должно быть " +
                        "PUBLISH_EVENT, REJECT_EVENT или null");
        }
    }

    private void changeCommonFields(UpdateEventAdminRequest eventDto, Event event) {
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getLocation() != null) {
            event.setLocation(eventDto.getLocation());
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
    }

    private void changeCategory(UpdateEventAdminRequest eventDto, Event event) {
        if (eventDto.getCategoryId() == null) {
            return;
        }
        Category category = categoryRepository.findById(eventDto.getCategoryId()).orElseThrow(
                () -> new NotFoundException("Категория не найдена"));
        event.setCategory(category);
    }

    private Map<Long, Long> getViewsMap(List<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        return getViewsByIds(eventIds);
    }

    private static void validateEvent(Event event) {
        if (event == null) {
            throw new NotFoundException("Событие не найдено");
        }
        if (!Objects.equals(event.getState(), EventState.PUBLISHED)) {
            throw new ValidationException("Событие должно быть опубликовано");
        }
    }

    private Event getEventWithGraphAndValidate(Long initiatorId, Long eventId) {
        EntityGraph<?> entityGraph = entityManager.getEntityGraph("event-entity-graph");
        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.fetchgraph", entityGraph);
        Event event = entityManager.find(Event.class, eventId, properties);
        validateEventAndInitiator(initiatorId, event);
        return event;
    }

    private static void validateEventAndInitiator(Long initiatorId, Event event) {
        if (event == null) {
            throw new NotFoundException("Событие не найдено");
        }
        if (!Objects.equals(initiatorId, event.getInitiator().getId())) {
            throw new ValidationException("Запрос может делать только пользователь, создавший событие (инициатор)");
        }
    }

    private void changeEventDate(UpdateEventAdminRequest eventDto, Event event) {
        if (eventDto.getEventDate() == null) {
            return;
        }
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plus(1, ChronoUnit.HOURS))) {
            throw new ValidationException(
                    "Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }
        event.setEventDate(eventDto.getEventDate());
    }

    private Map<Long, Long> getViewsByIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.size() == 0) {
            return null;
        }
        // подготавливаем данные
        String[] uri = new String[eventIds.size()];
        final String EVENTS = "/events/";
        for (int i = 0; i < eventIds.size(); i++) {
            uri[i] = EVENTS + eventIds.get(i);
        }
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.now();

        // запрашиваем статистику
        List<ViewStatsDto> statArray = statisticClient.getStatList(start, end, uri, false);

        // обрабатываем результат запроса
        int indexOfStartOfId = EVENTS.length();
        Map<Long, Long> result = new HashMap<>();
        for (ViewStatsDto hit : statArray) {
            String idString = hit.getUri().substring(indexOfStartOfId);
            Long id = Long.parseLong(idString);
            result.put(id, hit.getHits());
        }
        // возвращаем результат (события, у которых не было просмотров, не попадут в результат)
        return result;
    }

    private void addTextCategoriesAndPaid(String text, List<Long> categories, Boolean paid,
                                          JPAQuery<Event> query, QEvent qEvent) {
        if (text != null) {
            text = text.toLowerCase();
            query.where(qEvent.annotation.toLowerCase().contains(text)
                    .or(qEvent.description.toLowerCase().contains(text)));
        }
        if (categories != null) {
            query.where(qEvent.category.id.in(categories));
        }
        if (paid != null) {
            query.where(qEvent.paid.eq(paid));
        }
    }

    public List<EventShortDto> mapToEventShortDtos(List<Event> events, Boolean onlyAvailable) {
        // запрашиваем просмотры в сервисе статистики (будут получены только те события, у которых были просмотры)
        Map<Long, Long> viewsMap = getViewsMap(events);
        List<EventShortDto> eventDtos = new ArrayList<>();
        for (Event event : events) {
            long views = viewsMap.getOrDefault(event.getId(), 0L);
            long participantLimit = event.getParticipantLimit();
            long confirmedRequests = getConfirmedRequests(event, participantLimit);
            // если onlyAvailable == false, то добавляем все события (!onlyAvailable),
            // иначе добавляем только те события, у которых нет лимита участия (participantLimit == 0),
            // или у которых не исчерпан лимит участия (participantLimit - confirmedRequests) > 0)
            if (!onlyAvailable || (participantLimit == 0) || ((participantLimit - confirmedRequests) > 0)) {
                eventDtos.add(eventMapper.toEventShortDto(event, confirmedRequests, views));
            }
        }
        return eventDtos;
    }

    static long getConfirmedRequests(Event event, long participantLimit) {
        long confirmedRequests = 0;
        if (participantLimit != 0) {
            Collection<ParticipationRequest> requests = event.getRequests();
            confirmedRequests = requests.stream()
                    .filter(request -> Objects.equals(request.getStatus(), ParticipationRequestStatus.CONFIRMED))
                    .count();
        }
        return confirmedRequests;
    }

    private void addDates(LocalDateTime rangeStart, LocalDateTime rangeEnd,
                          JPAQuery<Event> query, QEvent qEvent) {
        // если не указана ни одна дата
        if (rangeStart == null && rangeEnd == null) {
            query.where(qEvent.eventDate.after(LocalDateTime.now()));
            // если получены обе даты
        } else if (rangeStart != null && rangeEnd != null) {
            validateDates(rangeStart, rangeEnd);
            query.where(qEvent.eventDate.between(rangeStart, rangeEnd));
            // если есть только дата начала периода
        } else if (rangeStart != null) {
            query.where(qEvent.eventDate.after(rangeStart));
            // если есть только дата окончания периода
        } else {
            query.where(qEvent.eventDate.before(rangeEnd));
        }
    }

    private void validateDates(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Дата начала периода не должна быть позже даты окончания периода," +
                    "за который нужно искать события");
        }
    }

    private void changeCommonFields(UpdateEventUserRequest updateRequest, Event event) {
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(updateRequest.getLocation());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
    }

    private void validateEventDto(UpdateEventUserRequest eventRequest, Long initiatorId, Event event) {
        if (eventRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Дата и время на которые намечено событие не может быть раньше," +
                    " чем через два часа от текущего момента");
        }
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Нельзя обновить уже опубликованное мероприятие");
        }
        if (!Objects.equals(event.getInitiator().getId(), initiatorId)) {
            throw new NotFoundException("Пользователь не является создателем мероприятия");
        }
    }

    private void changeStateAction(UpdateEventUserRequest updateRequestDto, Event event) {
        StateUserAction stateAction = updateRequestDto.getStateAction();
        if (stateAction == null) {
            return;
        }
        switch (stateAction) {
            case SEND_TO_REVIEW:
                event.setState(EventState.PENDING);
                break;
            case CANCEL_REVIEW:
                event.setState(EventState.CANCELED);
                break;
            default:
                throw new ValidationException(
                        "Состояние изменяемого события должно быть SEND_TO_REVIEW, CANCEL_REVIEW или null");
        }
    }

    private void changeCategory(UpdateEventUserRequest updateRequestDto, Event event) {
        if (updateRequestDto.getCategoryId() == null) {
            return;
        }
        Category category = categoryRepository.findById(updateRequestDto.getCategoryId()).orElseThrow(
                () -> new NotFoundException("Категория не найдена"));
        event.setCategory(category);
    }

    private static void validateRequests(List<ParticipationRequest> requests) {
        boolean notPending = requests.stream()
                .anyMatch(request ->
                        !Objects.equals(request.getStatus(), ParticipationRequestStatus.PENDING));
        if (notPending) {
            throw new ValidationException("Не все заявки находятся в состоянии ожидания");
        }
    }

    private void confirmRequests(List<ParticipationRequest> requests, Event event) {
        long confirmedRequestCount = event.getRequests().stream()
                .filter(request -> Objects.equals(request.getStatus(), ParticipationRequestStatus.CONFIRMED))
                .count();
        long participantLimit = event.getParticipantLimit();
        if (participantLimit <= confirmedRequestCount) {
            throw new ValidationException("Достигнут лимит одобренных заявок");
        }

        long currentLimit = participantLimit - confirmedRequestCount;
        for (ParticipationRequest request : requests) {
            if (currentLimit > 0) {
                request.setStatus(ParticipationRequestStatus.CONFIRMED);
            } else {
                request.setStatus(ParticipationRequestStatus.REJECTED);
            }
            --currentLimit;
        }
    }

    private Long getViewsOfOneEvent(Long eventId) {
        // подготавливаем данные
        String[] uri = {"/events/" + eventId};
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.now();

        // запрашиваем статистику
        ViewStatsDto[] statArray = statisticClient.getStatArray(start, end, uri, false);
        if (statArray.length == 1) {
            ViewStatsDto hitShortWithHitsDtoResponse = statArray[0];
            return hitShortWithHitsDtoResponse.getHits();
        }
        return 0L;
    }
}
