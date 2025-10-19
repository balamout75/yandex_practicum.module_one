package ru.yandex.practicum.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.configuration.DataSourceConfiguration;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.JdbcNativePostRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.util.ArrayList;
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

    /*
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
    }*/

    @Test
    void save_shouldAddPostWithTagsToDatabase() {
        PostDTO post = new PostDTO(8L,"Седьмой пост", "Бла", new String[]{"Байкал", "горы"}, "", 0L, 0L);
        postRepository.save(post);
        List<Post> all = postRepository.findAll(new ArrayList<>(), new ArrayList<>(), 1, 10);
        Post saved = all.stream().filter(u -> u.getId()==(7L)).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals(7L, saved.getId());
        assertEquals("Седьмой пост", saved.getTitle());
        assertEquals("Бла", saved.getText());
        assertEquals("", saved.getImage());
        assertEquals(0, saved.getLikesCount());
        assertEquals(0, saved.getCommentsCount());
        assertEquals(7L, saved.getTotal_records());
        assertEquals(2, saved.getTags().length);
        assertEquals("Байкал", saved.getTags()[0]);
        assertEquals("горы", saved.getTags()[1]);


        List<String> tags = postRepository.getTagsByPostId(7L);
        assertEquals(2, tags.size());
        assertEquals("Байкал", tags.get(0));
        assertEquals("горы", tags.get(1));
    }

    @Test
    void update_shouldUpdatePostWithTagsInDatabase() {
        long PostNumber = 6L;
        List<Post> all = postRepository.findAll(new ArrayList<>(), new ArrayList<>(), 1, 10);
        Post saved = all.stream().filter(u -> u.getId()==(PostNumber)).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals(PostNumber, saved.getId());
        assertEquals("Шестое сообщение", saved.getTitle());
        assertEquals("Бла бла бла", saved.getText());
        assertEquals("Неправильный файл", saved.getImage());
        assertEquals(3, saved.getLikesCount());
        assertEquals(0, saved.getCommentsCount());
        assertEquals(2, saved.getTags().length);
        assertEquals("Байкал", saved.getTags()[0]);
        assertEquals("горы", saved.getTags()[1]);
        PostDTO post = new PostDTO(PostNumber,"Шестое сообщение, исправленное", "Бла", new String[]{"аршан","горы", }, "Другой файл", 10L, 10L);
        postRepository.update(PostNumber,post);
        all = postRepository.findAll(new ArrayList<>(), new ArrayList<>(), 1, 10);
        saved = all.stream().filter(u -> u.getId()==(PostNumber)).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals(PostNumber, saved.getId());
        assertEquals("Шестое сообщение, исправленное", saved.getTitle());
        assertEquals("Бла", saved.getText());
        assertEquals("Неправильный файл", saved.getImage());
        assertEquals(3, saved.getLikesCount());
        assertEquals(0, saved.getCommentsCount());
        assertEquals(2, saved.getTags().length);
        assertEquals("Аршан", saved.getTags()[0]);
        assertEquals("горы", saved.getTags()[1]);

    }

    @Test
    void findAll_shouldReturnAllUsers() {
        List<Post> all = postRepository.findAll(new ArrayList<>(), new ArrayList<>(), 1, 10);
        assertNotNull(all);
        assertTrue(all.size()>=6);
        Post first = all.getFirst();
        assertEquals(1L, first.getId());
        assertEquals("Чистое синее озеро, белый песок, красочные скалы, хвойный лес — именно таким волшебным сочетанием природных даров отличается бух...",
                            first.getTitle());
        assertEquals("Бла", first.getText());
        assertEquals("Peschannaya.png", first.getImage());
        assertEquals(1, first.getLikesCount());
        assertEquals(3, first.getCommentsCount());
        assertTrue(first.getTotal_records()>=6);
        assertEquals(3, first.getTags().length);
        assertEquals("Байкал", first.getTags()[0]);
        assertEquals("Аршан", first.getTags()[1]);
        assertEquals("горы", first.getTags()[2]);
    }

    @Test
    void deleteById_shouldRemovePostFromDatabaseWithComments() {
        List<Post> users = postRepository.findAll(new ArrayList<>(), new ArrayList<>(), 1, 10);
        int initialSize = users.size();
        postRepository.deleteById(1L);
        users = postRepository.findAll(new ArrayList<>(), new ArrayList<>(), 1, 10);
        assertEquals(initialSize-1, users.size());
        assertTrue(users.stream().noneMatch(u -> u.getId()==1L));
    }

    @Test
    void existsById_true_and_false() {
        assertTrue(postRepository.existsById(2L));
        assertFalse(postRepository.existsById(999L));
    }
 }
