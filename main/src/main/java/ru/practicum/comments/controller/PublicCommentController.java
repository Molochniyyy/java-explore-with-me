package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentRequest;
import ru.practicum.comments.dto.CommentResponse;
import ru.practicum.comments.service.CommentService;
import ru.practicum.utils.ControllerLog;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class PublicCommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest commentRequest,
                                                      @PathVariable Long userId,
                                                      @PathVariable Long eventId,
                                                      HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        return new ResponseEntity<>(commentService.addComment(commentRequest, userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@RequestBody CommentRequest commentRequest,
                                                         @PathVariable Long userId,
                                                         @PathVariable Long eventId,
                                                         @PathVariable Long commentId,
                                                         HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        return new ResponseEntity<>(commentService.updateComment(commentRequest, commentId, eventId, userId), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @PathVariable Long userId,
                                              @PathVariable Long eventId,
                                              HttpServletRequest request) {
        log.info("\n\n{}\n", ControllerLog.createUrlInfo(request));
        commentService.deleteCommentByUser(commentId, userId, eventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
