package ru.yandex.practicum.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.DTO.CommentDTO;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    public Comment getById(Long id) { return commentRepository.getById(id); }


}
