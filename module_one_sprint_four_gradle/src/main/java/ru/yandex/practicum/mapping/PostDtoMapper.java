package ru.yandex.practicum.mapping;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.Tag;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.service.PostService;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostDtoMapper  {

    public PostDtoMapper() {   }

    //@Override
    public Post toEntity(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.title());
        post.setText(postDto.text());
        post.setText(postDto.text());
        return new Post();

    }
}
