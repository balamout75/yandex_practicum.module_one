package ru.yandex.practicum.repository;

import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.mapping.PostRowMapper;
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
        //String [] tags = new String[]{"#раз","#два","#три"};
        return jdbcTemplate.query("SELECT id, title, text, likesCount, commentsCount FROM posts",new PostRowMapper());
    }

    @Override
    public Post getById(Long id) {
        //String [] tags = new String[]{"#раз","#два","#три"};
        //String query = "SELECT * FROM posts WHERE id = ?";
        //Post post = jdbcTemplate.queryForObject(query,new PostRowMapper(),id);
        return jdbcTemplate.queryForObject("SELECT * FROM posts WHERE id = ?",new PostRowMapper(),id);
    }

    @Override
    public Resource getImageById(Long id) {

        return null;
    }

    @Override
    public Post save(PostDTO postDTO) {
        jdbcTemplate.update("insert into posts(title, text) values(?, ?)",
                postDTO.title(), postDTO.text());
        return getById()
    }

    @Override
    public void update(Long id, PostDTO postDTO) {
        jdbcTemplate.update("update posts set title = ?, text = ?, likesCount = ?, commentsCount = ? where id = ?",
                postDTO.title(), postDTO.text(), postDTO.likesCount(), postDTO.commentsCount(), id);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from posts where id = ?", id);
    }
}
