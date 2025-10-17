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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        mockMvc.perform(get("/api/posts/{postid}/comments/{id}",2L,1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['id']").value(4L))
                .andExpect(jsonPath("$['text']").value("2-1"))
                .andExpect(jsonPath("$['postId']").value(2L));

    }

    @Test
    void createComment_acceptsJson_andPersists() throws Exception {
        String json = """
                  {"text":"Четвертый комми","text":"Бла","tags":["Daniel","Craig"]}
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

    @Test
    void deleteUser_success() throws Exception {
        mockMvc.perform(delete("/api/posts/7"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/posts?search=&pageNumber=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['posts']",hasSize(6)));
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


}
