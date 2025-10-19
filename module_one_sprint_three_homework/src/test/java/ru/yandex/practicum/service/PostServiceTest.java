package ru.yandex.practicum.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.DTO.CommentDto;
import ru.yandex.practicum.configuration.DataSourceConfiguration;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.JdbcNativeCommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    public record Request(
            String inputString,
            List <String> words,
            List <String> tags
    ) {};

    static Stream<Arguments> applySearchString() {
        return Stream.of(
                Arguments.of(new Request("#гОры#Байкал",new ArrayList<>(), Arrays.asList("горы","байкал"))),
                Arguments.of(new Request("#гОры #Байкал",new ArrayList<>(), Arrays.asList("горы","байкал"))),
                Arguments.of(new Request(" #гОры #Байкал #",new ArrayList<>(), Arrays.asList("горы","байкал"))),
                Arguments.of(new Request("#гОры####Байкал#",new ArrayList<>(), Arrays.asList("горы","байкал"))),
                Arguments.of(new Request("Бухта песчанная #горы#байкал",Arrays.asList("бухта","песчанная"), Arrays.asList("горы","байкал"))),
                Arguments.of(new Request("Бухта #горы#байкал песчанная",Arrays.asList("бухта","песчанная"), Arrays.asList("горы","байкал")))
        );
    }
    @ParameterizedTest
    @MethodSource("applySearchString")
    void testSearchConditionParser(Request request) {
        //when(postRepository.findAll(anyList(),anyList(),anyInt(),anyInt())).thenReturn(new ArrayList<>());
        postService.findAll (request.inputString,1,1);
        verify(postRepository).findAll(request.words,request.tags,1,1);
    }
}
