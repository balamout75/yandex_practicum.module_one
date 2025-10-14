package ru.yandex.practicum.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.configuration.DataSourceConfiguration;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.JdbcNativePostRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativePostRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
class JdbcNativeUserRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        // чистим
        jdbcTemplate.execute("DELETE FROM users");
        // подготавливаем данные
        jdbcTemplate.update(
                "INSERT INTO users (id, first_name, last_name, age, active) VALUES (?,?,?,?,?)",
                1L, "Иван", "Иванов", 30, true
        );
        jdbcTemplate.update(
                "INSERT INTO users (id, first_name, last_name, age, active) VALUES (?,?,?,?,?)",
                2L, "Петр", "Петров", 25, false
        );
        jdbcTemplate.update(
                "INSERT INTO users (id, first_name, last_name, age, active) VALUES (?,?,?,?,?)",
                3L, "Мария", "Сидорова", 28, true
        );
    }

    @Test
    void save_shouldAddUserToDatabase() {
        Post user = new User(4L, "Петр", "Васильев", 25, true);

        postRepository.save(user);

        List<User> all = postRepository.findAll();
        User saved = all.stream().filter(u -> u.getId().equals(4L)).findFirst().orElse(null);

        assertNotNull(saved);
        assertEquals("Петр", saved.getFirstName());
        assertEquals("Васильев", saved.getLastName());
        assertEquals(25, saved.getAge());
        assertTrue(saved.isActive());
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        List<Post> posts = userRepository.findAll();

        assertNotNull(users);
        assertEquals(3, users.size());
        User first = users.getFirst();
        assertEquals(1L, first.getId());
        assertEquals("Иван", first.getFirstName());
    }

    @Test
    void deleteById_shouldRemoveUserFromDatabase() {
        userRepository.deleteById(1L);

        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
        assertTrue(users.stream().noneMatch(u -> u.getId().equals(1L)));
    }

    @Test
    void updateAvatar_and_findAvatarById() {
        byte[] avatar = new byte[]{1, 2, 3, 4};

        assertTrue(userRepository.updateAvatar(1L, avatar));

        byte[] fromDb = userRepository.findAvatarById(1L);
        assertArrayEquals(avatar, fromDb);
    }

    @Test
    void findAvatarById_returnsNull_whenNotSet() {
        assertNull(userRepository.findAvatarById(2L));
    }

    @Test
    void existsById_true_and_false() {
        assertTrue(userRepository.existsById(1L));
        assertFalse(userRepository.existsById(999L));
    }
}
