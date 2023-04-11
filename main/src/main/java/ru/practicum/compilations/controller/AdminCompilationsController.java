package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.service.CompilationService;
import ru.practicum.utils.ControllerLog;
import ru.practicum.utils.Create;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/admin/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdminCompilationsController {
    private final CompilationService service;

    @PostMapping
    ResponseEntity<CompilationDto> saveCompilation(
            @Validated({Create.class}) @RequestBody NewCompilationDto newCompilationDto,
            HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        CompilationDto result = service.addCompilation(newCompilationDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{compId}")
    ResponseEntity<CompilationDto> updateCompilation(
            @PathVariable Long compId,
            @RequestBody UpdateCompilationRequest updateCompilationRequest,
            HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        CompilationDto result = service.updateCompilation(updateCompilationRequest, compId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{compId}")
    ResponseEntity<Void> deleteCompilation(@PathVariable Long compId,
                                           HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        service.deleteCompilation(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
