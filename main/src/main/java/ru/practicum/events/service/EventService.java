package ru.practicum.events.service;

import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventSort;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface EventService {
    EventFullDto addEvent(NewEventDto eventDto, Long userId);

    Collection<EventShortDto> getEventsOfUser(Long userId, Integer from, Integer size);

    EventFullDto getFullEventOfUser(Long userId, Long eventId);

    EventFullDto updateEventByUser(UpdateEventUserRequest event, Long userId, Long eventId);

    Collection<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest request);

    Collection<EventFullDto> getEvents(List<Long> users,
                                       List<String> states,
                                       List<Long> categories,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Integer from,
                                       Integer size);

    Collection<EventShortDto> getEvents(String text,
                                        Boolean paid,
                                        Boolean onlyAvailable,
                                        EventSort sort,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Integer from,
                                        Integer size);

    EventFullDto getFullEvent(Long eventId);

    Map<Long, Long> getViews(Collection<Event> events);
}