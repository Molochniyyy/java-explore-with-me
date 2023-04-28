package ru.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.ParticipationRequestService;
import ru.practicum.utils.ControllerLog;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{requesterId}/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ParticipationRequestController {

    private final ParticipationRequestService service;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getPartRequestsByUser(@PathVariable Long requesterId,
                                                                        HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        List<ParticipationRequestDto> result = service.getAllRequestsOfUser(requesterId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> addRequest(@PathVariable Long requesterId,
                                                       @RequestParam Long eventId,
                                                       HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        ParticipationRequestDto result = service.addRequest(requesterId, eventId);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelPartRequest(@PathVariable Long requesterId,
                                                              @PathVariable Long requestId,
                                                              HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        ParticipationRequestDto result = service.cancelRequest(requesterId, requestId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
