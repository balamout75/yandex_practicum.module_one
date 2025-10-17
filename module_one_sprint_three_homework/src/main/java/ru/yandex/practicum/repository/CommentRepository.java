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

    private static final String SelectCommentsSQL = "SELECT id, text, postid FROM comments where postid = ?";
    private static final String SelectCommentsByIdSQL = "SELECT * FROM comments WHERE id = ?";
    private static final String CommentInsertingSQL="INSERT INTO comments(text, postid) VALUES(?, ?)";
    private static final String CommentUpdatingSQL="UPDATE comments SET text = ? WHERE id = ?";
    private static final String CommentDeletingSQL="DELETE FROM comments WHERE id = ?";
    private static final String CheckExistingCommentsSQL="SELECT COUNT(*) FROM posts, comments WHERE comments.postid=posts.id AND posts.id=? AND comments.id=?";

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        commentRowMapper= new CommentRowMapper();
    }


    public List<Comment> findAll(Long id) {
        return jdbcTemplate.query(SelectCommentsSQL,commentRowMapper,id);
    }

    public Comment getById(Long id) {
        return jdbcTemplate.queryForObject(SelectCommentsByIdSQL, commentRowMapper,id);
    }

    public Comment save(CommentDTO commentDTO) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(CommentInsertingSQL, new String[]{"id"});
                ps.setString(1, commentDTO.text());
                ps.setLong(2, commentDTO.postId());
                return ps;
            }
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        return getById(id);
    }

    public Comment update(Long id, CommentDTO commentDTO) {
        jdbcTemplate.update(CommentUpdatingSQL,
                commentDTO.text(), id);
        return getById(id);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(CommentDeletingSQL, id);
    }

    public boolean existsById(Long postId, Long commentId) {
        Integer Count = jdbcTemplate.queryForObject(CheckExistingCommentsSQL, Integer.class, postId, commentId);
        return Count != null && Count > 0;
    }
}
