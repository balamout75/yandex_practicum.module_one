package ru.yandex.practicum.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.mapping.CommentDtoMapper;
import ru.yandex.practicum.mapping.PostDtoMapper;
import ru.yandex.practicum.mapping.TagSearcher;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.repository.TagRepository;


@AutoConfiguration
public class WebConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(PostDtoMapper.class)
    public static class PostDtoMapperConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public PostDtoMapper postDtoMapper(TagSearcher tagSearcher) {
            return new PostDtoMapper(tagSearcher);
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(CommentDtoMapper.class)
    public static class CommentDtoMapperConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public CommentDtoMapper commentDtoMapper(PostRepository postRepository) {
            return new CommentDtoMapper(postRepository);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(TagSearcher.class)
    public static class TagSearcherConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public TagSearcher tagSearcher(TagRepository tagRepository) {
            return new TagSearcher(tagRepository);
        }
    }
}