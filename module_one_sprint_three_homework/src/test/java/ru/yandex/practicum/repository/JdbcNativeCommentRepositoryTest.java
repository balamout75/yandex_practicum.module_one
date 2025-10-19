package ru.yandex.practicum.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.DTO.CommentDto;
import ru.yandex.practicum.configuration.DataSourceConfiguration;
import ru.yandex.practicum.model.Comment;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcNativeCommentRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class JdbcNativeCommentRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentRepository commentRepository;

    static Stream<Arguments> AllCommentAtFirstPost() {
        return Stream.of(
                Arguments.of(new CommentDto(1,"1-1", 1)),
                Arguments.of(new CommentDto(2,"2-1", 1)),
                Arguments.of(new CommentDto(3,"3-1", 1))
        );
    }
    @ParameterizedTest
    @MethodSource("AllCommentAtFirstPost")
    void findAll_shouldReturnAllCommentsForFirstPost(CommentDto commentDto) {
        List<Comment> all = commentRepository.findAll(commentDto.postId());
        assertNotNull(all);
        assertEquals(3,all.size());
        Comment comment = all.get((int) (commentDto.id()-1));
        assertNotNull(comment);
        assertEquals(commentDto.id(), comment.getId());
        assertEquals(commentDto.text(), comment.getText());
        assertEquals(commentDto.postId(), comment.getPostId());
    }

    @ParameterizedTest
    @MethodSource("AllCommentAtFirstPost")
    void findEach_shouldReturnEachCommentsForFirstPost(CommentDto commentDto) {
        Comment comment=commentRepository.getById(commentDto.id());
        assertEquals(comment.getPostId(),commentDto.postId());
        assertEquals(comment.getText(),commentDto.text());
    }

    @ParameterizedTest
    @ValueSource(longs = {4L,111L})
    void findAll_shouldZeroList(long longs) {
        List<Comment> all = commentRepository.findAll(longs);
        assertTrue(all.isEmpty());
    }

    @Test
    void save_shouldAddCommentToPost() {
        long postId=2L;
        long total_records = jdbcTemplate.queryForList("Select count(*) from comments where POSTID = ?", Long.class, postId).stream()
                .findFirst()
                .orElse(0L);
        long last_record = jdbcTemplate.queryForList("Select max(id) from comments", Long.class).stream()
                .findFirst()
                .orElse(0L);
        CommentDto commentDto = new CommentDto(0L,"4-2", postId);
        commentRepository.save(commentDto);
        List<Comment> all = commentRepository.findAll(postId);
        Comment saved = all.stream().filter(u -> u.getText().equalsIgnoreCase("4-2")).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals(last_record+1, saved.getId());
        assertEquals("4-2", saved.getText());
        assertEquals(postId, saved.getPostId());
    }

    @Test
    void update_shouldUpdateCommentAtPost() {
        long postId = 2L;
        int commentNumber = 2;
        List<Comment> all = commentRepository.findAll(postId);
        Comment second = all.get(commentNumber-1);
        assertNotNull(second);
        long id=second.getId();
        assertEquals("2-2", second.getText());
        assertEquals(postId, second.getPostId());

        Comment comment = commentRepository.update(id,new CommentDto(id,"2-2, исправленное", postId));
        assertNotNull(comment);
        assertEquals(id, comment.getId());
        assertEquals("2-2, исправленное", comment.getText());
        assertEquals(postId, comment.getPostId());

        all = commentRepository.findAll(postId);
        second = all.get(commentNumber-1);
        assertNotNull(second);
        assertEquals(id,second.getId());
        assertEquals("2-2, исправленное", second.getText());
        assertEquals(postId, second.getPostId());
    }

    @Test
    void deleteById_shouldRemovePostFromDatabaseWithComments() {
        long postId = 2L;
        int commentNumber = 2;
        List<Comment> all = commentRepository.findAll(postId);
        int initialSize = all.size();
        Comment second = all.get(commentNumber-1);
        assertNotNull(second);
        long id=second.getId();
        assertTrue(second.getText().contains("2-2"));
        assertEquals(postId, second.getPostId());
        commentRepository.deleteById(id);
        all = commentRepository.findAll(postId);
        Comment newSecond = all.get(commentNumber-1);
        assertNotNull(newSecond);
        assertEquals(initialSize-1,all.size());
        assertNotEquals(id,newSecond.getId());
        assertEquals(("3-2"), newSecond.getText());
        assertEquals(postId, newSecond.getPostId());
    }

    @Test
    void existsById_true_and_false() {
        assertTrue(commentRepository.existsById(1L,1L));
        assertFalse(commentRepository.existsById(1L,10L));
        assertFalse(commentRepository.existsById(10L,1L));
        assertFalse(commentRepository.existsById(777L,777L));
    }
}
