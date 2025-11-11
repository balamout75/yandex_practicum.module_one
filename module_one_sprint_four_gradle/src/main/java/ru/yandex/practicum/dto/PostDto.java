package ru.yandex.practicum.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public record PostDto(

        @NotNull (groups = {Exist.class})
        @JsonProperty("id") Long id,
        @NotBlank (groups = {New.class, Exist.class})
        @JsonProperty("title") String title,
        @NotBlank @NotBlank (groups = {New.class, Exist.class})
        @JsonProperty("text") String text,
        @NotEmpty @NotEmpty (groups = {New.class, Exist.class})
        @JsonProperty("tags") String[] tags,
        @JsonProperty("likesCount") Long likesCount,
        @JsonProperty("commentsCount") Long commentsCount

) {

    public interface Exist {
    }

    public interface New {
    }

    @JsonCreator
    public PostDto(Long id, String title, String text, String[] tags, Long likesCount, Long commentsCount) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.tags = tags;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }
}