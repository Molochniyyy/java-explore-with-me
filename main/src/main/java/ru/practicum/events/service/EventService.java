package ru.practicum.events.service;

import org.springframework.stereotype.Service;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventSort;
import ru.practicum.events.model.EventState;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface EventService {
    EventFullDto addEvent(NewEventDto eventDto, Long userId);

    List<EventShortDto> getEventsOfUser(Long userId, Integer from, Integer size);

    EventFullDto getFullEventOfUser(Long userId, Long eventId);

    EventFullDto updateEventByUser(UpdateEventUserRequest event, Long userId, Long eventId);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest request);

    List<EventFullDto> getEvents(List<Long> users,
                                 List<EventState> states,
                                 List<Long> categories,
                                 LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd,
                                 Integer from,
                                 Integer size);

    List<EventShortDto> getEvents(String text,
                                  Boolean paid,
                                  Boolean onlyAvailable,
                                  EventSort sort,
                                  List<Long> categories,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Integer from,
                                  Integer size,
                                  String ip);

    EventFullDto getFullEvent(Long eventId, String ip);

    EventFullDto changeEventByAdmin(Long eventId, UpdateEventAdminRequest eventAdminRequest);

    Event getById(Long id);
}