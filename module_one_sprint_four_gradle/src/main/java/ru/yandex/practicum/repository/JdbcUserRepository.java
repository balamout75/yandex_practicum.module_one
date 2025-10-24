package ru.yandex.practicum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.User;

import java.util.List;

@Repository
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM users",
                (rs, rowNum) -> {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setAge(rs.getInt("age"));
                    user.setActive(rs.getBoolean("active"));
                    return user;
                }
        );
    }

    @Override
    public void save(User user) {
        jdbcTemplate.update(
                "INSERT INTO users(first_name, last_name, age, active) VALUES(?, ?, ?, ?)",
                user.getFirstName(), user.getLastName(), user.getAge(), user.isActive()
        );
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public void update(Long id, User user) {
        jdbcTemplate.update(
                "UPDATE users SET first_name=?, last_name=?, age=?, active=? WHERE id=?",
                user.getFirstName(), user.getLastName(), user.getAge(), user.isActive(), id
        );
    }
}
