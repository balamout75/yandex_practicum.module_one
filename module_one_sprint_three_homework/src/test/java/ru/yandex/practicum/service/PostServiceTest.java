package ru.yandex.practicum.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

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
    ) {}

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
        when(postRepository.findAll(anyList(),anyList(),anyInt(),anyInt())).thenReturn(new ArrayList<>());
        postService.findAll (request.inputString,1,1);
        verify(postRepository).findAll(request.words,request.tags,1,1);
    }
}
