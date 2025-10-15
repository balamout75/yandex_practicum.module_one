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
import ru.yandex.practicum.WebConfiguration;

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
                .andExpect(jsonPath("$['posts'][1].commentsCount").value(1))
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
                .andExpect(status().isOk());
                //.andExpect(content().string("image uploaded"));

        mockMvc.perform(get("/api/posts/{id}/image", 6L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(pngStub));
    }
    /*
    @Test
    void uploadAvatar_emptyFile_badRequest() throws Exception {
        MockMultipartFile empty = new MockMultipartFile("image", "empty.png", "image/png", new byte[0]);

        mockMvc.perform(multipart(HttpMethod.PUT,"/api/posts/{id}/image", 1L).file(empty))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadAvatar_userNotFound_404() throws Exception {
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
    }*/
}
