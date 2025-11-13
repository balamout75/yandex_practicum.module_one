package ru.yandex.practicum.repository;

import ru.yandex.practicum.dto.CommentDto;
import ru.yandex.practicum.model.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> findAll(Long id);

    Comment getById(Long id);

    Comment save(CommentDto commentDto);

    Comment update(Long id, CommentDto commentDto);

    void deleteById(Long id);

    boolean existsById(Long postId, Long commentId);
}
