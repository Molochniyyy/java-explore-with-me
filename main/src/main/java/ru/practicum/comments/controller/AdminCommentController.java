package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentResponse;
import ru.practicum.comments.service.CommentService;
import ru.practicum.utils.ControllerLog;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("admin/comments/{eventId}")
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentsOfEvent(@PathVariable Long eventId,
                                                                    HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        return new ResponseEntity<>(commentService.getCommentsOfEvent(eventId), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @PathVariable Long eventId,
                                              HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        commentService.deleteCommentByAdmin(commentId, eventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
