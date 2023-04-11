package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.UpdateEventAdminRequest;
import ru.practicum.events.model.EventState;
import ru.practicum.events.service.EventService;
import ru.practicum.utils.ControllerLog;
import ru.practicum.utils.Update;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdminEventController {
    private final EventService service;

    @GetMapping
    ResponseEntity<List<EventFullDto>> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        List<EventFullDto> result = service.getEvents(users, states, categories,
                rangeStart, rangeEnd, from, size);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PatchMapping(path = "/{eventId}")
    ResponseEntity<EventFullDto> changeEvent(@PathVariable Long eventId,
                                             @Validated({Update.class})
                                             @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                                             HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        EventFullDto result = service.changeEventByAdmin(eventId, updateEventAdminRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
