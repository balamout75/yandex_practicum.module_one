package ru.yandex.practicum.repository;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.configuration.DatabaseTestConfig;
import ru.yandex.practicum.configuration.MappingServiceTestConfig;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.mapping.PostDtoMapper;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.Tag;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(MappingServiceTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class PostRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostDtoMapper postDtoMapper;

    @Test
    @Order(1)
    void findPostByCondition_shouldReturnArrayOfPosts() {
        long totalRecords = jdbcTemplate.queryForList("Select count(*) from posts", Long.class).stream()
                .findFirst()
                .orElse(0L);
        List<Post> all = postRepository.findAll();
        assertNotNull(all);
        assertEquals(totalRecords,all.size());
        Post first = all.getFirst();
        assertNotNull(first);
        assertEquals(1L, first.getId());
        assertEquals("Чистое синее озеро, белый песок, красочные скалы, хвойный лес — именно таким волшебным сочетанием природных даров отличается бухта",
                first.getTitle().substring(0,130));
        assertEquals("Бла", first.getText());
        assertEquals("Peschannaya.png", first.getImage());
        assertEquals(1, first.getLikesCount());
        assertEquals(3, first.getComments().size());
        assertEquals(3, first.getTags().size());
        assertEquals("аршан", first.getTags().stream()
                        .map(Tag::getTag)
                        .sorted()
                        .toList()
                        .getFirst()
               );
        assertEquals("байкал", first.getTags().stream()
                .map(Tag::getTag)
                .sorted()
                .toList()
                .get(1)
        );
        assertEquals("горы", first.getTags().stream()
                .map(Tag::getTag)
                .sorted()
                .toList()
                .get(2)
        );
    }


    @Test
    @Order(2)
    void save_shouldAddPostWithTagsToDatabase() {
        long totalRecords = jdbcTemplate.queryForList("Select count(*) from posts", Long.class).stream()
                .findFirst()
                .orElse(0L);
        long lastRecord = jdbcTemplate.queryForList("Select max(id) from posts", Long.class).stream()
                .findFirst()
                .orElse(0L);
        PostDto postDto = new PostDto(0L,"Седьмой пост", "Бла", new String[]{"байкал", "горы"}, 0L, 0L);
        Post post = postDtoMapper.toEntity(postDto, new Post());
        postRepository.save(post);
        Post saved = postRepository.findAll().stream()
                .filter(x -> x.getId()==(lastRecord+1))
                .findFirst()
                .orElse(null);
        assertNotNull(saved);
        assertEquals(lastRecord+1, saved.getId());
        assertEquals("Седьмой пост", saved.getTitle());
        assertEquals("Бла", saved.getText());
        assertEquals("", saved.getImage());
        assertEquals(0, saved.getLikesCount());
        assertEquals(0, saved.getComments().size());
        assertEquals(2, saved.getTags().size());
        assertEquals("байкал", saved.getTags().stream()
                .map(Tag::getTag)
                .sorted()
                .toList()
                .get(0)
        );
        assertEquals("горы", saved.getTags().stream()
                .map(Tag::getTag)
                .sorted()
                .toList()
                .get(1)
        );
    }


    @Test
    @Order(3)
    void update_shouldUpdatePostWithTagsInDatabase() {
        long postNumber = 6L;
        Post saved = postRepository.findById(postNumber);
        assertNotNull(saved);
        assertEquals(postNumber, saved.getId());
        assertEquals("Шестое сообщение", saved.getTitle());
        assertEquals("Бла бла бла", saved.getText());
        assertEquals("Peschannaya.png", saved.getImage());
        assertEquals(3, saved.getLikesCount());
        assertEquals(0, saved.getComments().size());
        assertEquals(2, saved.getTags().size());
        assertEquals("байкал", saved.getTags().stream()
                .map(Tag::getTag)
                .sorted()
                .toList()
                .get(0)
        );
        assertEquals("горы", saved.getTags().stream()
                .map(Tag::getTag)
                .sorted()
                .toList()
                .get(1)
        );

        PostDto postDto = new PostDto(postNumber,"Шестое сообщение, исправленное", "Бла", new String[]{"аршан","горы", }, 10L, 10L);
        Post modifiedPost=postDtoMapper.toEntity(postDto, saved);
        postRepository.save(modifiedPost);

        saved = postRepository.findById(postNumber);
        assertNotNull(saved);
        assertEquals(postNumber, saved.getId());
        assertEquals("Шестое сообщение, исправленное", saved.getTitle());
        assertEquals("Бла", saved.getText());
        assertEquals("Peschannaya.png", saved.getImage());
        assertEquals(3, saved.getLikesCount());
        assertEquals(0, saved.getComments().size());
        assertEquals(2, saved.getTags().size());
        assertEquals("аршан", saved.getTags().stream()
                .map(Tag::getTag)
                .sorted()
                .toList()
                .get(0)
        );
        assertEquals("горы", saved.getTags().stream()
                .map(Tag::getTag)
                .sorted()
                .toList()
                .get(1)
        );
    }

    @Test
    @Order(4)
    void deleteById_shouldRemovePostFromDatabaseWithComments() {
        List<Post> users = postRepository.findAll();
        int initialSize = users.size();
        postRepository.deleteById(2L);
        users = postRepository.findAll();
        assertEquals(initialSize-1, users.size());
        assertTrue(users.stream().noneMatch(u -> u.getId()==2L));
    }

    @Test
    @Order(5)
    void existsById_true_and_false() {
        assertTrue(postRepository.existsById(1L));
        assertFalse(postRepository.existsById(999L));
    }
 }
