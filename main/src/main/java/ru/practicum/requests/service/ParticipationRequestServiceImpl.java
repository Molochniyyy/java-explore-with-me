package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventState;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapper.ParticipationRequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.ParticipationRequestStatus;
import ru.practicum.requests.repository.ParticipationRequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getAllRequestsOfUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));
        return requestRepository.findAllByRequester(user.getId());
    }

    @Transactional
    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие не найдено"));
        validateRequest(userId, event);
        ParticipationRequest participationRequest = new ParticipationRequest(user, event);
        requestRepository.save(participationRequest);
        return participationRequestMapper.toDto(participationRequest);
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest participationRequest = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос не найден или недоступен"));
        if (!userId.equals(participationRequest.getRequesterId())) {
            throw new ValidationException("Отменить можно только свой запрос");
        }
        participationRequest.setStatus(ParticipationRequestStatus.CANCELED);
        requestRepository.save(participationRequest);
        return participationRequestMapper.toDto(participationRequest);
    }

    private void validateRequest(Long requesterId, Event event) {
        if (Objects.equals(requesterId, event.getInitiator().getId())) {
            throw new ValidationException("Ошибка. Инициатор события не может делать запрос на участие в своём событии");
        }
        if (!Objects.equals(event.getState(), EventState.PUBLISHED)) {
            throw new ValidationException("Ошибка. Нельзя оставлять запросы на участие в неопубликованных событиях");
        }
        // если у события отсутствует ограничение на количество участников (==0), то проверки завершены
        if (Objects.equals(event.getParticipantLimit(), 0L)) {
            return;
        }
        // иначе проверяем, что ограничение на участников не превышено
        Long confirmedRequests = requestRepository.countAllByEventAndStatus(event,
                ParticipationRequestStatus.CONFIRMED);
        if ((event.getParticipantLimit() - confirmedRequests) <= 0L) {
            throw new ValidationException("Ошибка добавления запроса на участие. У события уже заполнен лимит участия");
        }
    }
}
