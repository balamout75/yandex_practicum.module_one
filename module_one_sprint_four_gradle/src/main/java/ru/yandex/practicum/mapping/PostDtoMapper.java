package ru.yandex.practicum.mapping;

import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.Post;

public class PostDtoMapper  {

    public PostDtoMapper() {   }

    //@Override
    public Post toEntity(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.title());
        post.setText(postDto.text());
        post.setImage("");
        post.setLikesCount(0L);
        return post;

    }
}
