package ru.yandex.practicum.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentDto(

        @NotNull(groups = {Exist.class})
        @JsonProperty("id") Long id,
        @NotBlank (groups = {New.class, Exist.class})
        @JsonProperty("text") String text,
        @NotNull(groups = {New.class, Exist.class})
        @JsonProperty("postId") Long postId
) {
    public interface Exist {
    }

    public interface New {
    }
    @JsonCreator
    public CommentDto(Long id, String text, Long postId) {
        this.id = id;
        this.text = text;
        this.postId = postId;
    }
}


