package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.configuration.DataSourceConfiguration;
import ru.yandex.practicum.configuration.WebConfiguration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringJUnitConfig(classes = {
        DataSourceConfiguration.class,
        WebConfiguration.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
class CommentControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
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
    void getCommentById_returnsJsonArray() throws Exception {
        mockMvc.perform(get("/api/posts/{postid}/comments/{id}",2L,4L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['id']").value(4L))
                .andExpect(jsonPath("$['text']").value("1-2"))
                .andExpect(jsonPath("$['postId']").value(2L));

    }

    @Test
    void createComment_returnsJsonArray_andPersists() throws Exception {
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
    void deleteСomment_success() throws Exception {
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
    void getUnexistingCommentById_isNotFound() throws Exception {
        mockMvc.perform(get("/api/posts/{postid}/comments/{id}",1L,4L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAlienСomment_isNotFound() throws Exception {
        Long AlienPostId=2L;
        mockMvc.perform(delete("/api/posts/{postid}/comments/{id}",AlienPostId,7L))
                .andExpect(status().isNotFound());
    }
    //Validator
    @ParameterizedTest
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
               mockMvc.perform(post(json,2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateIncorrectCommentById_isBadRequest() throws Exception {
        String json = """
                  {"id":4,"text":"Четвертый комментарий для второго сообщения","postId":2}
                """;
        mockMvc.perform(put("/api/posts/{postid}/comments/{id}",2L,6L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
