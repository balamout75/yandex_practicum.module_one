package ru.yandex.practicum.mapping;

import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.Tag;

public class PostEntityMapper  {

    public PostEntityMapper() {   }

    //@Override
    public PostDto toDto(Post post) {
        return new PostDto(post.getId(),
                post.getTitle(),
                post.getText(),
                post.getTags().stream()
                        .map(Tag::getTag)
                        .sorted()
                        .toArray(String[]::new),
                post.getLikesCount(),
                post.getComments().size());
    }
    public PostDto toDtoWithTitleForming(Post post) {
        String title=post.getTitle();
        if (title.length()>128) title=title.substring(0,128)+"...";
        return new PostDto(post.getId(),
                title,
                post.getText(),
                post.getTags().stream()
                        .map(Tag::getTag)
                        .sorted()
                        .toArray(String[]::new),
                post.getLikesCount(),
                post.getComments().size());
    }
}
