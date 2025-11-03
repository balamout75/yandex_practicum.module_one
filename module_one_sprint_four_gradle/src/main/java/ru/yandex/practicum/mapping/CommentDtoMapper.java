package ru.yandex.practicum.mapping;

import ru.yandex.practicum.dto.CommentDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.repository.PostRepository;

public class CommentDtoMapper {

    private final PostRepository postRepository;

    public CommentDtoMapper(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    //@Override
    public Comment toEntity(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.text());
        comment.setPost(postRepository.findById(commentDto.postId()));
        return comment;

    }
}

