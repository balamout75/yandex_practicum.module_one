package ru.yandex.practicum.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record PostDTO(
        @JsonProperty("id") long id,
        @JsonProperty("title") String title,
        @JsonProperty("text") String text,
        @JsonProperty("tags") String[] tags,
        @JsonProperty("likesCount") long likesCount,
        @JsonProperty("commentsCount") long commentsCount

) {
    @JsonCreator
    public PostDTO (long id, String title, String text, String[] tags){
        this(id, title, text, tags, -1L, -1L);
    }

    @JsonCreator
    public PostDTO(long id, String title, String text, String[] tags, long likesCount, long commentsCount) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.tags = tags;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }
}