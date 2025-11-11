package ru.yandex.practicum.repository;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.configuration.DatabaseTestConfig;
import ru.yandex.practicum.configuration.MappingServiceTestConfig;
import ru.yandex.practicum.dto.CommentDto;
import ru.yandex.practicum.mapping.CommentDtoMapper;
import ru.yandex.practicum.mapping.PostDtoMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(MappingServiceTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CommentRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    static Stream<Arguments> AllCommentAtFirstPost() {
        return Stream.of(
                Arguments.of(new CommentDto(1L,"1-1", 1L)),
                Arguments.of(new CommentDto(2L,"2-1", 1L)),
                Arguments.of(new CommentDto(3L,"3-1", 1L))
        );
    }
    @ParameterizedTest
    @Order(1)
    @MethodSource("AllCommentAtFirstPost")
    void findAll_shouldReturnAllCommentsForFirstPost(CommentDto commentDto) {
        Post post=postRepository.findById(commentDto.postId());
        List<Comment> all = commentRepository.findByPost(post);
        assertNotNull(all);
        assertEquals(3,all.size());
        Comment comment = all.get((int) (commentDto.id()-1));
        assertNotNull(comment);
        assertEquals(commentDto.id(), comment.getId());
        assertEquals(commentDto.text(), comment.getText());
        assertEquals(commentDto.postId(), comment.getPost().getId());
    }

    @ParameterizedTest
    @Order(2)
    @MethodSource("AllCommentAtFirstPost")
    void findEach_shouldReturnEachCommentsForFirstPost(CommentDto commentDto) {
        Comment comment=commentRepository.findById(commentDto.id());
        assertEquals(comment.getPost().getId(),commentDto.postId());
        assertEquals(comment.getText(),commentDto.text());
    }

    @ParameterizedTest
    @Order(3)
    @ValueSource(longs = {4L,111L})
    void findAll_shouldZeroList(long longs) {
        List<Comment> all = commentRepository.findByPost(postRepository.findById(longs));
        assertTrue(all.isEmpty());
    }

    @Test
    @Order(4)
    void save_shouldAddCommentToPost() {
        long postId=2L;
        long total_records = jdbcTemplate.queryForList("Select count(*) from comments where POSTID = ?", Long.class, postId).stream()
                .findFirst()
                .orElse(0L);
        long last_record = jdbcTemplate.queryForList("Select max(id) from comments", Long.class).stream()
                .findFirst()
                .orElse(0L);
        CommentDto commentDto = new CommentDto(0L,"4-2", postId);
        Comment comment = commentDtoMapper.toEntity(commentDto, new Comment());
        commentRepository.save(comment);
        List<Comment> all = commentRepository.findByPost(postRepository.findById(commentDto.postId()));
        Comment saved = all.stream().filter(u -> u.getText().equalsIgnoreCase("4-2")).findFirst().orElse(null);
        assertNotNull(saved);
        assertEquals(last_record+1, saved.getId());
        assertEquals("4-2", saved.getText());
        assertEquals(postId, saved.getPost().getId());
    }

    @Test
    @Order(5)
    void update_shouldUpdateCommentAtPost() {
        long postId = 2L;
        int commentNumber = 2;
        List<Comment> all = commentRepository.findByPost(postRepository.findById(postId));
        Comment targetComment = all.get(commentNumber-1);
        assertNotNull(targetComment);
        long id=targetComment.getId();
        assertEquals("2-2", targetComment.getText());
        assertEquals(postId, targetComment.getPost().getId());

        Comment updatedComment=commentDtoMapper.toEntity(new CommentDto(id,"2-2, исправленное", postId), targetComment);

        Comment comment = commentRepository.save(updatedComment);
        assertNotNull(comment);
        assertEquals(id, comment.getId());
        assertEquals("2-2, исправленное", comment.getText());
        assertEquals(postId, comment.getPost().getId());

        all = commentRepository.findByPost(postRepository.findById(postId));
        targetComment = all.get(commentNumber-1);
        assertNotNull(targetComment);
        assertEquals(id,targetComment.getId());
        assertEquals("2-2, исправленное", targetComment.getText());
        assertEquals(postId, targetComment.getPost().getId());
    }

    @Test
    @Order(6)
    void deleteById_shouldRemovePostFromDatabaseWithComments() {
        long postId = 2L;
        int commentNumber = 2;
        List<Comment> all = commentRepository.findByPost(postRepository.findById(postId));
        int initialSize = all.size();
        Comment second = all.get(commentNumber-1);
        assertNotNull(second);
        long id=second.getId();
        assertTrue(second.getText().contains("2-2"));
        assertEquals(postId, second.getPost().getId());
        commentRepository.deleteById(id);
        all = commentRepository.findByPost(postRepository.findById(postId));
        Comment newSecond = all.get(commentNumber-1);
        assertNotNull(newSecond);
        assertEquals(initialSize-1,all.size());
        assertNotEquals(id,newSecond.getId());
        assertEquals(("3-2"), newSecond.getText());
        assertEquals(postId, newSecond.getPost().getId());
    }

    @Test
    @Order(7)
    void existsById_true_and_false() {
        assertTrue(commentRepository.existsByPost_IdAndId(1L,1L));
        assertFalse(commentRepository.existsByPost_IdAndId(1L,10L));
        assertFalse(commentRepository.existsByPost_IdAndId(10L,1L));
        assertFalse(commentRepository.existsByPost_IdAndId(777L,777L));
    }
}
