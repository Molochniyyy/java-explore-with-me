package ru.practicum.comments.service;

import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.CommentRequest;
import ru.practicum.comments.dto.CommentResponse;

import java.util.List;

@Service
public interface CommentService {
    CommentResponse addComment(CommentRequest commentRequest, Long userId, Long eventId);

    List<CommentResponse> getCommentsOfEvent(Long eventId);

    CommentResponse updateComment(CommentRequest commentRequest, Long commentId, Long eventId, Long userId);

    void deleteCommentByUser(Long commentId, Long userId, Long eventId);

    void deleteCommentByAdmin(Long commentId, Long eventId);
}
