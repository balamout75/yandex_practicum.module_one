package ru.yandex.practicum.controller;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.configuration.DatabaseTestConfig;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void getComments_returnsJsonArray() throws Exception {
        mockMvc.perform(get("/api/posts/{postid}/comments",1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("1-1"))
                .andExpect(jsonPath("$[0].postId").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].text").value("2-1"))
                .andExpect(jsonPath("$[1].postId").value(1))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].text").value("3-1"))
                .andExpect(jsonPath("$[2].postId").value(1));
    }

    @Test
    @Order(2)
    void getCommentById_returnsJson() throws Exception {
        mockMvc.perform(get("/api/posts/{postid}/comments/{id}",2L,4L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['id']").value(4L))
                .andExpect(jsonPath("$['text']").value("1-2"))
                .andExpect(jsonPath("$['postId']").value(2L));

    }

    @Test
    @Order(3)
    void createComment_returnsJson_andPersists() throws Exception {
        String json = """
                  {"text":"Четвертый комментарий для второго сообщения","postId":2}
                """;
        mockMvc.perform(post("/api/posts/{postid}/comments",2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.text").value("Четвертый комментарий для второго сообщения"))
                .andExpect(jsonPath("$.postId").value(2L));

        mockMvc.perform(get("/api/posts/{postid}/comments/{id}",2L,10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.text").value("Четвертый комментарий для второго сообщения"))
                .andExpect(jsonPath("$.postId").value(2L));
    }

    @Test
    @Order(4)
    void updateComment_returnJson_andPersists() throws Exception {
        String json = """
                  {"id":6,"text":"Третий комментарий для второго сообщения, исправленный","postId":2}
                """;
        mockMvc.perform(get("/api/posts/{postid}/comments/{id}",2L,6L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.text").value("3-2"))
                .andExpect(jsonPath("$.postId").value(2L));

        mockMvc.perform(put("/api/posts/{postid}/comments/{id}",2L,6L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.text").value("Третий комментарий для второго сообщения, исправленный"))
                .andExpect(jsonPath("$.postId").value(2L));

        mockMvc.perform(get("/api/posts/{postid}/comments/{id}",2L,6L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.text").value("Третий комментарий для второго сообщения, исправленный"))
                .andExpect(jsonPath("$.postId").value(2L));
    }

    @Test
    @Order(5)
    void deleteComment_success() throws Exception {
        mockMvc.perform(get("/api/posts/{postid}/comments",3L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(7L))
                .andExpect(jsonPath("$[0].text").value("1-3"))
                .andExpect(jsonPath("$[0].postId").value(3L));
        mockMvc.perform(delete("/api/posts/{postid}/comments/{id}",3L,7L))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/posts/{postid}/comments",3L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(8L))
                .andExpect(jsonPath("$[0].text").value("2-3"))
                .andExpect(jsonPath("$[0].postId").value(3L));
    }

    //Aliens
    @Test
    @Order(6)
    void createAlienCommentById_isBadRequest() throws Exception {
        String json = """
                  {"text":"Четвертый комментарий для второго сообщения","postId":2}
                """;
        mockMvc.perform(post("/api/posts/{postid}/comments",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void updateAlienCommentById_isBadRequest() throws Exception {
        String json = """
                  {"id":4,"text":"Четвертый комментарий для второго сообщения","postId":2}
                """;
        mockMvc.perform(put("/api/posts/{postid}/comments/{id}",1L,6L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    void getUnexistingCommentById_isNotFound() throws Exception {
        mockMvc.perform(get("/api/posts/{postid}/comments/{id}",1L,4L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    void deleteAlienComment_isNotFound() throws Exception {
        Long alienPostId=2L;
        mockMvc.perform(delete("/api/posts/{postid}/comments/{id}",alienPostId,7L))
                .andExpect(status().isNotFound());
    }
    //Validator
    @ParameterizedTest
    @Order(10)
    @ValueSource(strings = {"""
                                {"text":"","postId":2}
                            """,
                            """
                                {"text":"Четвертый комментарий для второго сообщения","postId":0}
                            """,
                            """
                                {"text":"","postId":0}
                            """})
    void createIncorrectCommentById_isBadRequest(String json) throws Exception {
               mockMvc.perform(post("/api/posts/{postid}/comments",2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
    @ParameterizedTest
    @Order(11)
    @ValueSource(strings = {"""
                                {"id":6,"text":"Четвертый комментарий для второго сообщения","postId":2}
                            """,
                            """
                                {"id":4,"text":"","postId":2}
                            """,
                            """
                                {"id":4,"text":"Четвертый комментарий для второго сообщения","postId":}
                            """,
                            """
                                {"id":,"text":"Четвертый комментарий для второго сообщения","postId":2}
                            """,
                            """
                                {"text":"Четвертый комментарий для второго сообщения","postId":2}
                            """})
    void updateIncorrectCommentById_isBadRequest(String json) throws Exception {

        mockMvc.perform(put("/api/posts/{postid}/comments/{id}",2L,4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
