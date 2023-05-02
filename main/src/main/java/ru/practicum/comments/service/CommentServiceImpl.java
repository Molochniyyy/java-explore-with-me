package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentRequest;
import ru.practicum.comments.dto.CommentResponse;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.service.EventService;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.users.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserService userService;
    private final EventService eventService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public CommentResponse addComment(CommentRequest commentRequest, Long userId, Long eventId) {
        Comment comment = commentMapper.toEntity(commentRequest);
        comment.setCommentator(userService.getById(userId));
        comment.setEvent(eventService.getById(eventId));
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Override
    public List<CommentResponse> getCommentsOfEvent(Long eventId) {
        return commentMapper.toResponses(eventService.getById(eventId).getComments());
    }

    @Transactional
    @Override
    public CommentResponse updateComment(CommentRequest commentRequest, Long commentId, Long eventId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментарий не найден")
        );
        if (!comment.getCommentator().getId().equals(userId)) {
            throw new ConflictException("Редактировать комментарий может только его автор");
        }
        comment.setDescription(commentRequest.getDescription());
        eventService.getById(eventId);
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public void deleteCommentByUser(Long commentId, Long userId, Long eventId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментарий не найден")
        );
        eventService.getById(eventId);
        if (!comment.getCommentator().getId().equals(userId)) {
            throw new ConflictException("Удалить комментарий может только его автор или админ");
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional
    @Override
    public void deleteCommentByAdmin(Long commentId, Long eventId) {
        eventService.getById(eventId);
        commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментарий не найден")
        );
        commentRepository.deleteById(commentId);
    }
}
