package ru.yandex.practicum.mapping;

import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.Tag;

public class PostEntityMapper  {

    public PostEntityMapper() {   }

    public PostDto toDto(Post post) {
        return toDtoOverall(post.getTitle(),post);

    }
    public PostDto toDtoWithTitleForming(Post post) {
        String title=post.getTitle();
        return toDtoOverall((title.length()>128)?(title.substring(0,128)+"..."):title,post);
    }
    private PostDto toDtoOverall(String title,Post post) {
        return new PostDto(post.getId(),
                title,
                post.getText(),
                post.getTags().stream()
                        .map(Tag::getTag)
                        .sorted()
                        .toArray(String[]::new),
                post.getLikesCount(),
                (long) post.getComments().size());
    }
}
