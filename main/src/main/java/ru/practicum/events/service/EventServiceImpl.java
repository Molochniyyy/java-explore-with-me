package ru.practicum.events.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final EntityManager entityManager;

    private final StatisticClient statisticClient;

    @Override
    public EventFullDto addEvent(NewEventDto eventDto, Long userId) {
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Дата и время на которые намечено событие не может быть раньше," +
                    " чем через два часа от текущего момента");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Category category = categoryRepository.findById(eventDto.getCategoryId()).orElseThrow(
                () -> new NotFoundException("Категория не найдена"));
        return EventMapper.toFullDto(eventRepository.save(EventMapper.toEvent(eventDto, user, category)), 0L);
    }

    @Override
    public Collection<EventShortDto> getEventsOfUser(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        Collection<Event> events = eventRepository.findByInitiatorId(userId, pageable).getContent();
        return new ArrayList<>(EventMapper.toShortDto(events, this.getViews(events)));
    }

    @Override
    public EventFullDto getFullEventOfUser(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        return EventMapper.toFullDto(event, this.getViews(List.of(event)));
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
        return EventMapper.toFullDto(updateEvent, getViews(List.of(updateEvent)));
    }

    @Override
    public Collection<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является создателем мероприятия");
        }
        return ParticipationRequestMapper.toDto(event.getRequests());
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
                        .map(ParticipationRequestMapper::toDto)
                        .collect(Collectors.partitioningBy(
                                request2 -> Objects.equals(request2.getStatus(), ParticipationRequestStatus.CONFIRMED)));

        return new EventRequestStatusUpdateResult(requestDtos.get(true), requestDtos.get(false));
    }

    @Override
    public Collection<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                              Integer from, Integer size) {
        return null;
    }

    @Override
    public Collection<EventShortDto> getEvents(String text, Boolean paid, Boolean onlyAvailable, EventSort sort,
                                               List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Integer from, Integer size) {
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

        if (sort!=null && sort.equals(EventSort.VIEWS)){
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }
        statisticClient.postStat("ewm-main-service", "/events", )
        return null;
    }

    @Override
    public EventFullDto getFullEvent(Long eventId) {
        return null;
    }

    @Override
    public Map<Long, Long> getViews(Collection<Event> events) {
        List<String> uris = events.stream()
                .map(Event::getId)
                .map(id -> "/events/" + id.toString())
                .collect(Collectors.toUnmodifiableList());
        List<ViewStatsDto> eventStats = statisticClient.getStats(uris);
        return eventStats.stream()
                .filter(viewStatsDto -> viewStatsDto.getApp().equals("ewm-service"))
                .collect(Collectors.toMap(viewStatsDto -> {
                    Pattern pattern = Pattern.compile("/events/([0-9]*)");
                    Matcher matcher = pattern.matcher(viewStatsDto.getUri());
                    return Long.parseLong(matcher.group(1));
                }, ViewStatsDto::getHits));
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

    private List<EventShortDto> mapToEventShortDtos(List<Event> events, Boolean onlyAvailable) {
        // запрашиваем просмотры в сервисе статистики (будут получены только те события, у которых были просмотры)
        Map<Long, Long> viewsMap = getViews(events);
        List<EventShortDto> eventDtos = new ArrayList<>();
        for (Event event : events) {
            long views = viewsMap.getOrDefault(event.getId(), 0L);
            long participantLimit = event.getParticipantLimit();
            long confirmedRequests = getConfirmedRequests(event, participantLimit);
            // если onlyAvailable == false, то добавляем все события (!onlyAvailable),
            // иначе добавляем только те события, у которых нет лимита участия (participantLimit == 0),
            // или у которых не исчерпан лимит участия (participantLimit - confirmedRequests) > 0)
            if (!onlyAvailable || (participantLimit == 0) || ((participantLimit - confirmedRequests) > 0)) {
                eventDtos.add(EventMapper.toShortDto(event, views));
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
}
