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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.DTO.PostDto;
import ru.yandex.practicum.configuration.DataSourceConfiguration;
import ru.yandex.practicum.configuration.WebConfiguration;
import ru.yandex.practicum.model.Post;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class PostControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    //CRUD
    @Test
    void getPosts_returnsJsonArray() throws Exception {
        long total_records = jdbcTemplate.queryForList("Select count(*) from posts", Long.class).stream()
                .findFirst()
                .orElse(0L);
        Long last_records = jdbcTemplate.queryForList("Select max(id) from posts", Long.class).stream()
                .findFirst()
                .orElse(0L);
        long pageSize=total_records-1;
        //pageSize=10;

        mockMvc.perform(get("/api/posts?search=&pageNumber=1&pageSize="+pageSize))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['posts']",hasSize((int) pageSize)))
                .andExpect(jsonPath("$['posts'][0].title").value("Чистое синее озеро, белый песок, красочные скалы, хвойный лес — именно таким волшебным сочетанием природных даров отличается бух..."))
                .andExpect(jsonPath("$['posts'][0].commentsCount").value(3))
                .andExpect(jsonPath("$['posts'][1].text").value("Бла бла"))
                .andExpect(jsonPath("$['posts'][1].likesCount").value(2))
                .andExpect(jsonPath("$['posts'][1].commentsCount").value(3))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.lastPage").value(2));

        mockMvc.perform(get("/api/posts?search=&pageNumber=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['posts']",hasSize((int) total_records)))
                .andExpect(jsonPath("$['posts'][0].title").value("Чистое синее озеро, белый песок, красочные скалы, хвойный лес — именно таким волшебным сочетанием природных даров отличается бух..."))
                .andExpect(jsonPath("$['posts'][0].commentsCount").value(3))
                .andExpect(jsonPath("$['posts'][1].text").value("Бла бла"))
                .andExpect(jsonPath("$['posts'][1].likesCount").value(2))
                .andExpect(jsonPath("$['posts'][1].commentsCount").value(3))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.lastPage").value(1));
    }

    @Test
    void getPostById_returnsJsonArray() throws Exception {
        mockMvc.perform(get("/api/posts/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['title']").value("Второе сообщение"))
                .andExpect(jsonPath("$['tags']",hasSize(1)))
                .andExpect(jsonPath("$['tags'][0]").value("байкал"))
                .andExpect(jsonPath("$['text']").value("Бла бла"))
                .andExpect(jsonPath("$['likesCount']").value(2))
                .andExpect(jsonPath("$['commentsCount']").value(3));
    }

    @Test
    void createPost_acceptsJson_andPersists() throws Exception {
        String json = """
                  {"title":"Седьмое сообщение","text":"О чем то","tags":["Daniel","Craig"]}
                """;
        long total_records = jdbcTemplate.queryForList("Select count(*) from posts", Long.class).stream()
                .findFirst()
                .orElse(0L);
        long last_record = jdbcTemplate.queryForList("Select max(id) from posts", Long.class).stream()
                .findFirst()
                .orElse(0L);
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(last_record+1))
                .andExpect(jsonPath("$['title']").value("Седьмое сообщение"))
                .andExpect(jsonPath("$['text']").value("О чем то"))
                .andExpect(jsonPath("$['tags']",hasSize(2)))
                .andExpect(jsonPath("$['tags'][0]").value("Daniel"))
                .andExpect(jsonPath("$['tags'][1]").value("Craig"));
        mockMvc.perform(get("/api/posts?search=&pageNumber=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['posts']",hasSize((int) (total_records+1))));
    }

    @Test
    void updatePost_acceptsJson_andPersists() throws Exception {
        long postId=6L;
        mockMvc.perform(get("/api/posts/"+postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['title']").value("Шестое сообщение"))
                .andExpect(jsonPath("$['text']").value("Бла бла бла"))
                .andExpect(jsonPath("$['tags']",hasSize(2)))
                .andExpect(jsonPath("$['tags'][0]").value("байкал"))
                .andExpect(jsonPath("$['tags'][1]").value("горы"))
                .andExpect(jsonPath("$['likesCount']").value(3))
                .andExpect(jsonPath("$['commentsCount']").value(0));
        String json = """
                        {"id":""" +postId+"""
                            ,"title":"Шестое сообщение, исправленное","text":"Бла","tags":["Daniel","Craig"]}""";
        mockMvc.perform(put("/api/posts/"+postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.title").value("Шестое сообщение, исправленное"))
                .andExpect(jsonPath("$.text").value("Бла"))
                .andExpect(jsonPath("$.likesCount").value(3l))
                .andExpect(jsonPath("$.commentsCount").value(0))
                .andExpect(jsonPath("$['tags']",hasSize(2)))
                .andExpect(jsonPath("$['tags'][0]").value("Daniel"))
                .andExpect(jsonPath("$['tags'][1]").value("Craig"));

        mockMvc.perform(get("/api/posts/"+postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.title").value("Шестое сообщение, исправленное"))
                .andExpect(jsonPath("$.text").value("Бла"))
                .andExpect(jsonPath("$.likesCount").value(3l))
                .andExpect(jsonPath("$.commentsCount").value(0))
                .andExpect(jsonPath("$['tags']",hasSize(2)))
                .andExpect(jsonPath("$['tags'][0]").value("Daniel"))
                .andExpect(jsonPath("$['tags'][1]").value("Craig"));
    }

    @Test
    void deletePost_success() throws Exception {
        long postIdAndNum=3L;
        long total_records = jdbcTemplate.queryForList("Select count(*) from posts", Long.class).stream()
                .findFirst()
                .orElse(0L);
        mockMvc.perform(get("/api/posts?search=&pageNumber=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['posts']",hasSize((int) (total_records))))
                .andExpect(jsonPath("$['posts']["+(postIdAndNum-1)+"]['title']").value("третье сообщение"))
                .andExpect(jsonPath("$['posts']["+(postIdAndNum-1)+"]['text']").value("Бла бла бла"))
                .andExpect(jsonPath("$['posts']["+(postIdAndNum-1)+"]['tags']",hasSize(2)))
                .andExpect(jsonPath("$['posts']["+(postIdAndNum-1)+"]['tags'][0]").value("аршан"))
                .andExpect(jsonPath("$['posts']["+(postIdAndNum-1)+"]['tags'][1]").value("горы"))
                .andExpect(jsonPath("$['posts']["+(postIdAndNum-1)+"]['likesCount']").value(10))
                .andExpect(jsonPath("$['posts']["+(postIdAndNum-1)+"]['commentsCount']").value(3));
        mockMvc.perform(delete("/api/posts/"+postIdAndNum))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/posts?search=&pageNumber=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['posts']",hasSize((int) (total_records-1))))
                .andExpect(jsonPath("$['posts']["+(postIdAndNum-1)+"]['title']").value("Четвертое сообщение"))
                .andExpect(jsonPath("$['posts']["+(postIdAndNum-1)+"]['text']").value("Бла бла"))
                .andExpect(jsonPath("$['posts']["+(postIdAndNum-1)+"]['tags']",hasSize(1)))
                .andExpect(jsonPath("$['posts']["+(postIdAndNum-1)+"]['tags'][0]").value("горы"))
                .andExpect(jsonPath("$['posts']["+(postIdAndNum-1)+"]['likesCount']").value(2))
                .andExpect(jsonPath("$['posts']["+(postIdAndNum-1)+"]['commentsCount']").value(0));
    }

    @Test
    void uploadAndDownloadImage_success() throws Exception {
        byte[] pngStub = new byte[]{(byte) 137, 80, 78, 71};
        long postId=6L;
        MockMultipartFile file = new MockMultipartFile("image", "picture.png", "image/png", pngStub);
        mockMvc.perform(multipart(HttpMethod.PUT,"/api/posts/{id}/image", postId).file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Image uploaded"));

        mockMvc.perform(get("/api/posts/{id}/image", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(pngStub));
    }

    @Test
    void getImage_postHasImage() throws Exception {
        mockMvc.perform(get("/api/posts/{id}/image", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    void uploadImage_emptyFile_badRequest() throws Exception {
        MockMultipartFile empty = new MockMultipartFile("image", "empty.png", "image/png", new byte[0]);
        mockMvc.perform(multipart(HttpMethod.PUT,"/api/posts/{id}/image", 3L).file(empty))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadImage_postNotFound_404() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "image.png", "image/png", new byte[]{1, 2, 3});
        mockMvc.perform(multipart(HttpMethod.PUT,"/api/posts/{id}/image", 999L).file(file))
                .andExpect(status().isNotFound());
    }

    @Test
    void getImage_postHasNoImage_404() throws Exception {
        mockMvc.perform(get("/api/posts/{id}/image", 6L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getImage_postNotFound_404() throws Exception {
        mockMvc.perform(get("/api/posts/{id}/image", 777L))
                .andExpect(status().isNotFound());
    }

    @Test
    void Post_like() throws Exception {
        mockMvc.perform(post("/api/posts/{id}/likes",1L))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void Post_like_postNotFound_404() throws Exception {
        mockMvc.perform(post("/api/posts/{id}/likes",777L))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"""
                                {"title":"Present","text":"Present","tags":[]}
                            """,
                            """
                                {"title":"Present","text":"","tags":["Something 1","Something 2"]}
                            """,
                            """
                                {"title":"","text":"Present","tags":["Something 1","Something 2"]}
                            """,
                            """
                                {"title":"","text":"","tags":[]}
                            """})
    void creatIncorrectPost_isBadRequest(String json) throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"""
                                {"title":"Present","text":"Present","tags":["Something 1","Something 2"]}""",
                            """
                                {"id":3,"title":"Present","text":"Present","tags":["Something 1","Something 2"]}""",
                            """
                                {"id":2,"title":"","text":"Present","tags":["Something 1","Something 2"]}""",
                            """
                                {"id":2,"title":"Present","text":"","tags":["Something 1","Something 2"]}""",
                            """
                                {"id":2,"title":"Present","text":"Present","tags":[]}""",
                            """
                                {"id":2,"title":"","text":"","tags":[]}"""})
    void updateIncorrectPost_isBadRequest(String json) throws Exception {
        long postId=2;
        mockMvc.perform(put("/api/posts/"+postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
