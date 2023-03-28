package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatController {

    private final StatService service;
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

    @PostMapping("/hit")
    public EndpointHitDto saveHit(@RequestBody @Valid EndpointHitDto hitDto) {
        return service.saveHit(hitDto);
    }

    @GetMapping
    public List<ViewStatsDto> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        return service.getStats(LocalDateTime.parse(start, formatter),
                LocalDateTime.parse(end, formatter),
                uris, unique);
    }
}
