package ru.yandex.practicum.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentDto(
    @JsonProperty("id") long id,
    @JsonProperty("text") String text,
    @JsonProperty("postId") long postId
) {
    @JsonCreator
    public CommentDto(long id, String text, long postId) {
        this.id = id;
        this.text = text;
        this.postId = postId;
    }
}


