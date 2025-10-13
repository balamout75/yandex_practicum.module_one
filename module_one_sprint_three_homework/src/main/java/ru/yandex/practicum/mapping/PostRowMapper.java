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
        String [] tags = new String[]{"#раз","#два","#три"};
        Long id=rs.getLong("id");
        Post post = new Post(id,
                rs.getString("title"),
                rs.getString("text"),
                postRepository.getTagsByPostId(id).toArray(String[]::new),
                rs.getString("image"),
                rs.getLong("likescount"),
                postRepository.getPostsCommentsCountById(id));
        return post;
    }
}
