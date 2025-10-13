package ru.yandex.practicum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.DTO.CommentDTO;
import ru.yandex.practicum.mapping.CommentRowMapper;
import ru.yandex.practicum.model.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CommentRowMapper commentRowMapper;

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        commentRowMapper= new CommentRowMapper();
    }


    public List<Comment> findAll(Long id) {
        //String [] tags = new String[]{"#раз","#два","#три"};
        return jdbcTemplate.query("SELECT id, text, postid FROM comments where postid = ?",commentRowMapper,id);
    }

    public Comment getById(Long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM comments WHERE id = ?", commentRowMapper,id);
    }

    public Comment save(CommentDTO commentDTO) {
        String PostInsertingSQL="insert into comments(text, postid) values(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(PostInsertingSQL, new String[]{"id"});
                ps.setString(1, commentDTO.text());
                ps.setLong(2, commentDTO.postId());
                return ps;
            }
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        return getById(id);
    }

    public Comment update(Long id, CommentDTO commentDTO) {
        jdbcTemplate.update("update comments set text = ? where id = ?",
                commentDTO.text(), id);
        return getById(id);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from comments where id = ?", id);
    }
}
