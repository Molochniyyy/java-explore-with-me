package ru.practicum.controller;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatisticService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping
public class StatisticController {
    private final StatisticService statisticService;

    @PostMapping("/hit")
    public ResponseEntity<?> saveStat(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("Запрос на сохранение информации об обращении к эндпоинту {}", endpointHitDto.getUri());
        statisticService.saveStatistic(endpointHitDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStat(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                      LocalDateTime start,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                      LocalDateTime end,
                                      @RequestParam(required = false) List<String> uris,
                                      @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Запрос на получение статистики");
        return statisticService.getStatistic(start, end, uris, unique);
    }
}
