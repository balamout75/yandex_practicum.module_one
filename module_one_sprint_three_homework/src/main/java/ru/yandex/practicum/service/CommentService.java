package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.DTO.CommentDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.repository.JdbcNativeCommentRepository;

import java.util.List;

@Service
public class CommentService {

    private final JdbcNativeCommentRepository jdbcNativeCommentRepository;

    public CommentService(JdbcNativeCommentRepository jdbcNativeCommentRepository) {
        this.jdbcNativeCommentRepository = jdbcNativeCommentRepository;
    }

    public List<Comment> findAll(Long id) {
        return jdbcNativeCommentRepository.findAll(id);
    }

    public Comment save(CommentDto commentDto) {
        return jdbcNativeCommentRepository.save(commentDto);
    }

    public Comment update(Long id, CommentDto commentDto) {
        return jdbcNativeCommentRepository.update(id, commentDto);
    }

    public void deleteById(Long id) { jdbcNativeCommentRepository.deleteById(id); }

    public Comment getById(Long id) { return jdbcNativeCommentRepository.getById(id); }

    public boolean existsById(Long postId, Long commentId) { return jdbcNativeCommentRepository.existsById(postId, commentId); }

}
