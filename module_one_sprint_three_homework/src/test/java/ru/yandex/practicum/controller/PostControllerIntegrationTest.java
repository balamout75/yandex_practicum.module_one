package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

        // Чистим и наполняем БД перед каждым тестом
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("""
                    INSERT INTO posts (title, text, image, likesCount) 
                    VALUES ('Первое сообщение', 'Бла', 'Peschannaya.png', 1)
                """);
        jdbcTemplate.execute("""
                    insert into posts(title, text, image, likesCount) values ('Второе сообщение', 'Бла бла','Peschannaya.png', 2);
                """);
    }

    INSERT INTO posts (title, text, image, likesCount) values ('Первое сообщение', 'Бла', 'Peschannaya.png', 1);
    insert into posts(title, text, image, likesCount) values ('Второе сообщение', 'Бла бла','Peschannaya.png', 2);

    @Test
    void getUsers_returnsJsonArray() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Иван"))
                .andExpect(jsonPath("$[1].firstName").value("Мария"));
    }

    @Test
    void createUser_acceptsJson_andPersists() throws Exception {
        String json = """
                  {"id":3,"firstName":"Анна","lastName":"Смирнова","age":28,"active":true}
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.firstName").value("Анна"));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void deleteUser_noContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void uploadAndDownloadAvatar_success() throws Exception {
        byte[] pngStub = new byte[]{(byte) 137, 80, 78, 71};
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", pngStub);

        mockMvc.perform(multipart("/api/users/{id}/avatar", 1L).file(file))
                .andExpect(status().isCreated())
                .andExpect(content().string("ok"));

        mockMvc.perform(get("/api/users/{id}/avatar", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(pngStub));
    }

    @Test
    void uploadAvatar_emptyFile_badRequest() throws Exception {
        MockMultipartFile empty = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        mockMvc.perform(multipart("/api/users/{id}/avatar", 1L).file(empty))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("empty file"));
    }

    @Test
    void uploadAvatar_userNotFound_404() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/users/{id}/avatar", 999L).file(file))
                .andExpect(status().isNotFound())
                .andExpect(content().string("user not found"));
    }

    @Test
    void getAvatar_userHasNoAvatar_404() throws Exception {
        mockMvc.perform(get("/api/users/{id}/avatar", 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAvatar_userNotFound_404() throws Exception {
        mockMvc.perform(get("/api/users/{id}/avatar", 777L))
                .andExpect(status().isNotFound());
    }
}
