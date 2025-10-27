package ru.yandex.practicum.mapping;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.Tag;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.service.PostService;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostEntityMapper  {

    public PostEntityMapper() {   }

    //@Override
    public PostDto toDto(Post post) {
        return new PostDto(post.getId(),
                post.getTitle(),
                post.getText(),
                post.getTags().stream()
                        .map(Tag::getTag)
                        .toArray(String[]::new),
                post.getLikesCount(),
                post.getComments().size());
    }
}
