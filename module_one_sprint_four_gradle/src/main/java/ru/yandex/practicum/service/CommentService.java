package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mapping.CommentDtoMapper;
import ru.yandex.practicum.mapping.CommentEntityMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.dto.CommentDto;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentDtoMapper commentDtoMapper;
    private final CommentEntityMapper commentEntityMapper;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        commentEntityMapper = new CommentEntityMapper();
        commentDtoMapper = new CommentDtoMapper(postRepository);
    }

    public List<CommentDto> findAll(Long id) {
        return commentRepository.findByPost(postRepository.findById(id)).stream()
                .map(commentEntityMapper::toDto)
                .toList();
    }

    public CommentDto save(CommentDto commentDto) {
        Comment comment= commentDtoMapper.toEntity(commentDto);
        return commentEntityMapper.toDto(commentRepository.save(comment));
    }

    public CommentDto update(Long id, CommentDto commentDto) {
        //return сommentRepository.update(id, commentDto);
        return null;
    }

    @Transactional
    public void deleteById(Long postId, Long commentId) {
        Post post = postRepository.findById(postId);
        commentRepository.deleteById(commentId);
        postRepository.save(post);
    }

    public CommentDto getById(Long id) {
        return commentEntityMapper.toDto(commentRepository.findById(id));
    }

    public boolean existsById(Long postId, Long commentId) { return true; }

}
