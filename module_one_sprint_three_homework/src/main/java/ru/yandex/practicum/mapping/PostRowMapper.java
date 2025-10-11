package ru.yandex.practicum.mapping;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.model.Post;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostRowMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        String [] tags = new String[]{"#раз","#два","#три"};
        Post post = new Post(rs.getLong("id"),
                rs.getString("title"),
                rs.getString("text"),
                tags,
                rs.getString("image"),
                rs.getLong("likesCount"),
                rs.getLong("likesCount"));
        return post;
    }
}
