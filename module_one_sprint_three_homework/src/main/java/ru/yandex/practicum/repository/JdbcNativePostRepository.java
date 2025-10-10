package ru.yandex.practicum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.model.Post;

import java.util.List;

@Repository
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> findAll() {
        return jdbcTemplate.query(
                "select id, title, text, likesCount, commentsCount from posts",
                (rs, rowNum) -> new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("text"),
                        rs.getLong("likesCount"),
                        rs.getLong("likesCount")
                ));
    }

    @Override
    public void save(PostDTO postDTO) {
        jdbcTemplate.update("insert into posts(title, text, likesCount, commentsCount) values(?, ?, ?, ?)",
                postDTO.title(), postDTO.text(), postDTO.likesCount(), postDTO.commentsCount());
    }

    @Override
    public void update(Long id, PostDTO postDTO) {
        jdbcTemplate.update("update posts set title = ?, text = ?, lekesCount = ?, commentsCount = ? where id = ?",
                postDTO.title(), postDTO.text(), postDTO.likesCount(), postDTO.commentsCount(), id);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from posts where id = ?", id);
    }
}
