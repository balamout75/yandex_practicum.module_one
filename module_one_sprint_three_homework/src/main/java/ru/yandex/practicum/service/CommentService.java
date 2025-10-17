package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.DTO.CommentDTO;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> findAll(Long id) {
        return commentRepository.findAll(id);
    }

    public Comment save(CommentDTO commentDTO) {
        return commentRepository.save(commentDTO);
    }

    public Comment update(Long id, CommentDTO commentDTO) {
        return commentRepository.update(id, commentDTO);
    }

    public void deleteById(Long id) { commentRepository.deleteById(id); }

    public Comment getById(Long id) { return commentRepository.getById(id); }

    public boolean existsById(Long postId, Long commentId) { return commentRepository.existsById(postId, commentId); }

}
