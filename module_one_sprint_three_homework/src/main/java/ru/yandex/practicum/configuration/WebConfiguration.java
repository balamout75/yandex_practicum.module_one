package ru.yandex.practicum.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.yandex.practicum.validator.CommentDtoValidator;
import ru.yandex.practicum.validator.PostDtoValidator;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"ru.yandex.practicum"})
@PropertySource("classpath:application.properties")
/*public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }
}*/

public class WebConfiguration{
    @Bean
    public PostDtoValidator postDtoValidator() {
        return new PostDtoValidator();
    }
    @Bean
    public CommentDtoValidator commentDtoValidator() {
        return new CommentDtoValidator();
    }
};