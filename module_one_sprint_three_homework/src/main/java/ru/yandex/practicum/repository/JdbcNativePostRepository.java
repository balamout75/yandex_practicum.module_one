package ru.yandex.practicum.repository;

import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.mapping.PostRowMapper;
import ru.yandex.practicum.model.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PostRowMapper postRowMapper;
    private static final String SelectSQL = "SELECT id, title, text, image, likesCount, commentsCount FROM posts";
    private static final String InsertSQL="insert into posts(title, text) values(?, ?)";
    private static final String UpdateByIdSQL = "update posts set title = ?, text = ?  where id = ?";
    private static final String UpdateImageByIdSQL = "update posts set image = ? where id = ?";
    private static final String SelectByIdSQL = "SELECT * FROM posts WHERE id = ?";
    private static final String DeleteByIdSQL = "delete from posts where id = ?";

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        postRowMapper=new PostRowMapper();
    }

    @Override
    public List<Post> findAll() {
        return jdbcTemplate.query(SelectSQL,postRowMapper);
    }

    @Override
    public Post getById(Long id) {
        return jdbcTemplate.queryForObject(SelectByIdSQL, postRowMapper,id);
    }

    @Override
    public Post save(PostDTO postDTO) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(InsertSQL, new String[]{"id"});
                ps.setString(1, postDTO.title());
                ps.setString(2, postDTO.text());
                return ps;
            }
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        return getById(id);
    }

    @Override
    public Post update(Long id, PostDTO postDTO) {
        jdbcTemplate.update(UpdateByIdSQL,
                postDTO.title(), postDTO.text(), id);
        return getById(id);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DeleteByIdSQL, id);
    }

    @Override
    public String getFileNameByPostId(Long id) {
        //тут просится Optional
        return jdbcTemplate.queryForObject(SelectByIdSQL, postRowMapper,id).getImage();
    }

    @Override
    public boolean setFileNameByPostId(Long id, String fileName) {
        jdbcTemplate.update(UpdateImageByIdSQL, fileName, id);
       return true;
    }
}
