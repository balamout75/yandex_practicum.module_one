package ru.yandex.practicum.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
class PostServiceTest {
    @MockitoBean
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    public record Request(
            String inputString,
            String words,
            List <String> tags,
            int method
    ) {}

    //  Тестируем парсер поисковой строки
    //  В зависимости от поискового запроса вызываются разные методы репозитория.
    //  конкретика определяется переменной method :
    //1 есть подстрока поиска и тэги
    //2 только подстрока
    //3 только тэги
    //4 ищем все, но так не бывает
    static Stream<Arguments> applySearchString() {
        return Stream.of(
                Arguments.of(new Request("Бухта песчанная #горы#байкал","бухта песчанная", Arrays.asList("байкал","горы"),1)),
                Arguments.of(new Request("Бухта #горы#байкал песчанная","бухта песчанная", Arrays.asList("байкал","горы"), 1)),
                Arguments.of(new Request("гОры Байкал","горы байкал", new ArrayList<>(),2)),
                Arguments.of(new Request("Бухта Песчанная     Байкал", "бухта песчанная байкал", new ArrayList<>(),2)),
                Arguments.of(new Request("#гОры#Байкал","", Arrays.asList("байкал","горы"),3)),
                Arguments.of(new Request("#гОры #Байкал #ГОры", "", Arrays.asList("байкал","горы"),3)),
                Arguments.of(new Request("#гОры #Байкал", "", Arrays.asList("байкал","горы"),3)),
                Arguments.of(new Request(" #гОры #Байкал #", "", Arrays.asList("байкал","горы"),3)),
                Arguments.of(new Request("#гОры####Байкал#", "", Arrays.asList("байкал","горы"),3))
        );
    }
    @ParameterizedTest
    @MethodSource("applySearchString")
    void testSearchConditionParser(Request request) {
        List<Post> posts = new ArrayList<>();
        Page<Post> pagedResponse = new PageImpl<>(posts);
        Pageable pageable = PageRequest.of(0, 5);
        //Mock target methods
        when(postRepository.findBySearchStringAndAllTags(anyString(), anyList(),anyInt(), any())).thenReturn(pagedResponse);
        when(postRepository.findByTitleContainingIgnoreCase(anyString(), any())).thenReturn(pagedResponse);
        when(postRepository.findByAllTags(anyList(),anyInt(), any())).thenReturn(pagedResponse);

        //run with searchString
        postService.findAll(request.inputString(), pageable);

        //test target method invocation
        switch (request.method()) {
            case 1 -> verify(postRepository).findBySearchStringAndAllTags(eq("%"+request.words()+"%"),eq(request.tags()),eq(request.tags().size()), any(Pageable.class));
            case 2 -> verify(postRepository).findByTitleContainingIgnoreCase(eq(request.words()), any(Pageable.class));
            case 3 -> verify(postRepository).findByAllTags(eq(request.tags()),eq(request.tags().size()), any(Pageable.class));
        };
    }
}
