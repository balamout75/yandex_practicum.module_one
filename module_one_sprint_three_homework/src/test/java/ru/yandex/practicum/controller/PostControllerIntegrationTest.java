package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        mockMvc.perform(get("/api/posts?search=&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['posts']",hasSize(5)))
                .andExpect(jsonPath("$['posts'][0].title").value("Чистое синее озеро, белый песок, красочные скалы, хвойный лес — именно таким волшебным сочетанием природных даров отличается бух..."))
                .andExpect(jsonPath("$['posts'][0].commentsCount").value(3))
                .andExpect(jsonPath("$['posts'][1].text").value("Бла бла"))
                .andExpect(jsonPath("$['posts'][1].image").value("Peschannaya.png"))
                .andExpect(jsonPath("$['posts'][1].likesCount").value(2))
                .andExpect(jsonPath("$['posts'][1].commentsCount").value(3))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.lastPage").value(2));
    }

    @Test
    void getPostById_returnsJsonArray() throws Exception {
        mockMvc.perform(get("/api/posts/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['title']").value("Второе сообщение"))
                .andExpect(jsonPath("$['tags']",hasSize(1)))
                .andExpect(jsonPath("$['tags'][0]").value("Байкал"))
                .andExpect(jsonPath("$['text']").value("Бла бла"))
                .andExpect(jsonPath("$['image']").value("Peschannaya.png"))
                .andExpect(jsonPath("$['likesCount']").value(2))
                .andExpect(jsonPath("$['commentsCount']").value(1));
    }

    @Test
    void createPost_acceptsJson_andPersists() throws Exception {
        String json = """
                  {"title":"Седьмое сообщение","text":"Бла","tags":["Daniel","Craig"]}
                """;
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$['tags']",hasSize(2)))
                .andExpect(jsonPath("$['tags'][1]").value("Craig"));

        mockMvc.perform(get("/api/posts?search=&pageNumber=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['posts']",hasSize(7)));
    }

    @Test
    void updatePost_acceptsJson_andPersists() throws Exception {
        String json = """
                  {"title":"Седьмое сообщение","text":"Бла","tags":["Daniel","Craig"]}
                """;
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$['tags']",hasSize(2)))
                .andExpect(jsonPath("$['tags'][1]").value("Craig"));

        mockMvc.perform(get("/api/posts?search=&pageNumber=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['posts']",hasSize(7)));
    }

    @Test
    void deletePost_success() throws Exception {
        mockMvc.perform(get("/api/posts?search=горы&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$['title']").value("третье сообщение"))
                .andExpect(jsonPath("$['tags']",hasSize(2)))
                .andExpect(jsonPath("$['tags'][1]").value("горы"))
                .andExpect(jsonPath("$['text']").value("Бла бла бла"))
                .andExpect(jsonPath("$['image']").value("Peschannaya.png"))
                .andExpect(jsonPath("$['likesCount']").value(3));
        mockMvc.perform(delete("/api/posts/5"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/posts?search=#горы&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$['title']").value("Четвертое сообщение"))
                .andExpect(jsonPath("$['tags']",hasSize(2)))
                .andExpect(jsonPath("$['tags'][0]").value("горы"))
                .andExpect(jsonPath("$['text']").value("Бла бла"))
                .andExpect(jsonPath("$['image']").value("Peschannaya.png"))
                .andExpect(jsonPath("$['likesCount']").value(2));
    }

    @Test
    void uploadAndDownloadImage_success() throws Exception {
        byte[] pngStub = new byte[]{(byte) 137, 80, 78, 71};
        MockMultipartFile file = new MockMultipartFile("image", "picture.png", "image/png", pngStub);
        mockMvc.perform(multipart(HttpMethod.PUT,"/api/posts/{id}/image", 6L).file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Image uploaded"));

        mockMvc.perform(get("/api/posts/{id}/image", 6L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(pngStub));
    }

    @Test
    void getImage_postHasImage() throws Exception {
        mockMvc.perform(get("/api/posts/{id}/image", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
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
        ;
    }

    @Test
    void updateAlienPostById_isBadRequest() throws Exception {
        String json = """
                  {"id":4,"text":"Четвертый комментарий для второго сообщения","postId":2}
                """;
        mockMvc.perform(put("/api/posts/{postid}/comments/{id}",1L,6L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPost_emptyText() throws Exception {
        String json = """
                  {"title":"Восьмое сообщение","text":"","tags":["Daniel","Craig"]}
                """;
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPost_emptyTitle() throws Exception {
        String json = """
                  {"title":"","text":"Нечто","tags":["Daniel","Craig"]}
                """;
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPost_emptyTag() throws Exception {
        String json = """
                  {"title":"Нечто","text":"Нечто","tags":[]}
                """;
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
