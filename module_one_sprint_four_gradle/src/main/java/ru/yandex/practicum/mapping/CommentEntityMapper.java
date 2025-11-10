package ru.yandex.practicum.mapping;

import ru.yandex.practicum.dto.CommentDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.repository.PostRepository;

public class CommentEntityMapper  {

    public CommentEntityMapper() {   }

    //@Override
    public CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getPost().getId());
    }
}
