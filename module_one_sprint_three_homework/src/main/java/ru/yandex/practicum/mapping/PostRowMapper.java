package ru.yandex.practicum.mapping;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostRowMapper implements RowMapper<Post> {

    private final PostRepository postRepository;

    public PostRowMapper(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id=rs.getLong("id");
        return new Post(id,
                rs.getString("title"),
                rs.getString("text"),
                postRepository.getTagsByPostId(id).toArray(String[]::new),
                rs.getString("image"),
                rs.getLong("likescount"),
                postRepository.getPostsCommentsCountById(id),
                rs.getLong("total_records"));
    }
}
