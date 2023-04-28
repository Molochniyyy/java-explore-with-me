package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.*;
import ru.practicum.events.service.EventService;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.utils.ControllerLog;
import ru.practicum.utils.Create;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{initiatorId}/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PrivateEventController {
    private final EventService service;

    @GetMapping(path = "/{eventId}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable Long initiatorId,
                                              @PathVariable Long eventId,
                                              HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        EventFullDto result = service.getFullEventOfUser(initiatorId, eventId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsByUser(@PathVariable Long initiatorId,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        List<EventShortDto> result = service.getEventsOfUser(initiatorId, from, size);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EventFullDto> saveEvent(@PathVariable Long initiatorId,
                                           @Validated({Create.class}) @RequestBody NewEventDto newEventDto,
                                           HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        EventFullDto result = service.addEvent(newEventDto, initiatorId);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{eventId}")
    public ResponseEntity<EventFullDto> changeEvent(@PathVariable Long initiatorId, @PathVariable Long eventId,
                                             @RequestBody UpdateEventUserRequest updateRequestDto) {
        log.info("\n\nПолучен запрос к эндпоинту: PATCH /users/{}/events/{}/\n" +
                "Создан объект из тела запроса:\n'{}'", initiatorId, eventId, updateRequestDto);
        EventFullDto result = service.updateEventByUser(updateRequestDto, initiatorId, eventId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(path = "/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsOfEvent(@PathVariable Long initiatorId,
                                                                     @PathVariable Long eventId,
                                                                     HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        List<ParticipationRequestDto> result = service.getEventRequests(initiatorId, eventId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PatchMapping(path = "/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> changeStatusOfRequestsOfEvent(
            @PathVariable Long initiatorId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest updateRequestDto,
            HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        EventRequestStatusUpdateResult result = service.changeRequestStatus(
                initiatorId, eventId, updateRequestDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
