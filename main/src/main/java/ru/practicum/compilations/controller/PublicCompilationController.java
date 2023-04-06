package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.CompilationService;
import ru.practicum.utils.ControllerLog;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PublicCompilationController {
    private final CompilationService service;

    @GetMapping
    ResponseEntity<List<CompilationDto>> getAllCompilations(
            @RequestParam(defaultValue = "false") Boolean pinned,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer fromElement,
            @Positive @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        List<CompilationDto> result = service.findCompilations(pinned, fromElement, size);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(path = "/{compId}")
    ResponseEntity<CompilationDto> getCompilationById(@PathVariable Long compId,
                                                      HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        CompilationDto result = service.findCompilationById(compId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
