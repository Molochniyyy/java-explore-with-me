package ru.practicum.requests.service;

import org.springframework.stereotype.Service;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

@Service
public interface ParticipationRequestService {
    List<ParticipationRequestDto> getAllRequestsOfUser(Long userId);

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long eventId);
}
