package ru.yandex.practicum.mapping;

import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.Tag;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class PostDtoMapper  {

    private TagSearcher tagSearcher;

    public PostDtoMapper(TagSearcher tagSearcher) { this.tagSearcher = tagSearcher; }

    //@Override
    public Post toEntity(PostDto postDto, Post post) {
        post.setTitle(postDto.title());
        post.setText(postDto.text());
        String[] tags = postDto.tags();
        Set<Tag> tags1 = Arrays.stream(tags)
                .distinct()
                .map(tagSearcher::searchTag)
                .collect(Collectors.toSet());
        post.setTags(tags1);
        return post;
    }
}
