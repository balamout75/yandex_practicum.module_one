package ru.yandex.practicum.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentDTO(
    @JsonProperty("id") long id,
    @JsonProperty("text") String text,
    @JsonProperty("postid") long postid
) {
    @JsonCreator
    public CommentDTO(long id, String text, long postid) {
        this.id = id;
        this.text = text;
        this.postid = postid;
    }
}


