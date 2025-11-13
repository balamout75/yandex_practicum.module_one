package ru.yandex.practicum.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.configuration.DataSourceConfiguration;
import ru.yandex.practicum.model.Post;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativePostRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class JdbcNativePostRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @Test
    void findPostByCondition_shouldReturnArrayOfPosts() {
        long total_records = jdbcTemplate.queryForList("Select count(*) from posts", Long.class).stream()
                .findFirst()
                .orElse(0L);
        List<Post> all = postRepository.findAll(new ArrayList<>(), new ArrayList<>(), 1, 10);
        assertNotNull(all);
        assertEquals(total_records,all.size());
        Post first = all.getFirst();
        assertNotNull(first);
        assertEquals(1L, first.getId());
        assertEquals("Чистое синее озеро, белый песок, красочные скалы, хвойный лес — именно таким волшебным сочетанием природных даров отличается бух...",
                first.getTitle());
        assertEquals("Бла", first.getText());
        assertEquals("Peschannaya.png", first.getImage());
        assertEquals(1, first.getLikesCount());
        assertEquals(3, first.getCommentsCount());
        assertEquals(total_records, first.getTotal_records());
        assertEquals(3, first.getTags().length);
        assertEquals("байкал", first.getTags()[0]);
        assertEquals("аршан", first.getTags()[1]);
        assertEquals("горы", first.getTags()[2]);
    }

    @Test
    void save_shouldAddPostWithTagsToDatabase() {
        long total_records = jdbcTemplate.queryForList("Select count(*) from posts", Long.class).stream()
                .findFirst()
                .orElse(0L);
        long last_record = jdbcTemplate.queryForList("Select max(id) from posts", Long.class).stream()
                .findFirst()
                .orElse(0L);
        PostDto post = new PostDto(0L,"Седьмой пост", "Бла", new String[]{"байкал", "горы"}, 0L, 0L);
        postRepository.save(post);
        List<Post> all = postRepository.findAll(new ArrayList<>(), new ArrayList<>(), 1, 10);
        Post saved = all.stream().filter(u -> u.getId()==(7L)).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals(last_record+1, saved.getId());
        assertEquals("Седьмой пост", saved.getTitle());
        assertEquals("Бла", saved.getText());
        assertEquals("", saved.getImage());
        assertEquals(0, saved.getLikesCount());
        assertEquals(0, saved.getCommentsCount());
        assertEquals(total_records+1, saved.getTotal_records());
        assertEquals(2, saved.getTags().length);
        assertEquals("байкал", saved.getTags()[0]);
        assertEquals("горы", saved.getTags()[1]);

        List<String> tags = postRepository.getTagsByPostId(7L);
        assertEquals(2, tags.size());
        assertEquals("байкал", tags.get(0));
        assertEquals("горы", tags.get(1));
    }

    @Test
    void update_shouldUpdatePostWithTagsInDatabase() {
        long postNumber = 6L;
        List<Post> all = postRepository.findAll(new ArrayList<>(), new ArrayList<>(), 1, 10);
        Post saved = all.stream().filter(u -> u.getId()==(postNumber)).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals(postNumber, saved.getId());
        assertEquals("Шестое сообщение", saved.getTitle());
        assertEquals("Бла бла бла", saved.getText());
        assertEquals("Неправильный файл", saved.getImage());
        assertEquals(3, saved.getLikesCount());
        assertEquals(0, saved.getCommentsCount());
        assertEquals(2, saved.getTags().length);
        assertEquals("байкал", saved.getTags()[0]);
        assertEquals("горы", saved.getTags()[1]);

        PostDto post = new PostDto(postNumber,"Шестое сообщение, исправленное", "Бла", new String[]{"аршан","горы", }, 10L, 10L);
        postRepository.update(postNumber,post);

        all = postRepository.findAll(new ArrayList<>(), new ArrayList<>(), 1, 10);
        saved = all.stream().filter(u -> u.getId()==(postNumber)).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals(postNumber, saved.getId());
        assertEquals("Шестое сообщение, исправленное", saved.getTitle());
        assertEquals("Бла", saved.getText());
        assertEquals("Неправильный файл", saved.getImage());
        assertEquals(3, saved.getLikesCount());
        assertEquals(0, saved.getCommentsCount());
        assertEquals(2, saved.getTags().length);
        assertEquals("аршан", saved.getTags()[0]);
        assertEquals("горы", saved.getTags()[1]);
    }

    @Test
    void deleteById_shouldRemovePostFromDatabaseWithComments() {
        List<Post> users = postRepository.findAll(new ArrayList<>(), new ArrayList<>(), 1, 10);
        int initialSize = users.size();
        postRepository.deleteById(2L);
        users = postRepository.findAll(new ArrayList<>(), new ArrayList<>(), 1, 10);
        assertEquals(initialSize-1, users.size());
        assertTrue(users.stream().noneMatch(u -> u.getId()==2L));
    }

    @Test
    void existsById_true_and_false() {
        assertTrue(postRepository.existsById(2L));
        assertFalse(postRepository.existsById(999L));
    }
 }
