package ru.yandex.practicum.mapping;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.service.PostService;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostEntityMapper  {

    private final PostService postService;

    public PostEntityMapper(PostService postService) {
        this.postService = postService;
    }

    @Override
    public PostDto mapRow(Post) {
        return new PostDto(id,
                rs.getString("title"),
                rs.getString("text"),
                postRepository.getTagsByPostId(id).toArray(String[]::new),
                rs.getString("image"),
                rs.getLong("likescount"),
                postRepository.getPostsCommentsCountById(id),
                rs.getLong("total_records"));
    }
}
