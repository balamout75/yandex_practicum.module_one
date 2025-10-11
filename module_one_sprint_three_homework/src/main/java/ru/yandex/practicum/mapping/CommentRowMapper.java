package ru.yandex.practicum.mapping;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommentRowMapper implements RowMapper<Comment> {
    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Comment comment = new Comment(rs.getLong("id"),
                rs.getString("text"),
                rs.getLong("postid"));
            return comment;
    }
}
