package ru.yandex.practicum.configuration;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.mapping.CommentDtoMapper;
import ru.yandex.practicum.mapping.PostDtoMapper;
import ru.yandex.practicum.mapping.TagSearcher;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.repository.TagRepository;

@TestConfiguration
public class MappingServiceTestConfig {

    /*
    @Bean
    public DataRepository mockDataRepository() {
        return Mockito.mock(DataRepository.class);
    }
    */
    //Зарегистрировал бин специально для теста, можно было и так его создать
    @Bean
    public CommentDtoMapper commentDtoMapper(PostRepository postRepository) {
        return new CommentDtoMapper(postRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public PostDtoMapper postDtoMapper(TagSearcher tagSearcher) {
        return new PostDtoMapper(tagSearcher);
    }

    @Bean
    @ConditionalOnMissingBean
    public TagSearcher tagSearcher(TagRepository tagRepository) {
        return new TagSearcher(tagRepository);
    }

}