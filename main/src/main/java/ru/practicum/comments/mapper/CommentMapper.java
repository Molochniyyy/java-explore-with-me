package ru.practicum.comments.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.comments.dto.CommentRequest;
import ru.practicum.comments.dto.CommentResponse;
import ru.practicum.comments.model.Comment;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toEntity(CommentRequest commentRequest);

    CommentResponse toResponse(Comment comment);

    List<CommentResponse> toResponses(List<Comment> comments);
}
