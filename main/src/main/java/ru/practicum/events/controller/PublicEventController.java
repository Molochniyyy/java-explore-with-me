package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.EventSort;
import ru.practicum.events.service.EventService;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.utils.ControllerLog;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PublicEventController {
    private final EventService service;

    @GetMapping(path = "{eventId}")
    ResponseEntity<EventFullDto> getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        EventFullDto result = service.getFullEvent(eventId, request.getRemoteAddr());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping
    ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        EventSort eventSort;
        if (sort != null) {
            eventSort = EventSort.from(sort).orElseThrow(
                    () -> new NotFoundException("Вид сортировки не найден."));
        } else {
            eventSort = null;
        }
        List<EventShortDto> result = service.getEvents(text, paid, onlyAvailable, eventSort, categories, rangeStart,
                rangeEnd, from, size, request.getRemoteAddr());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
