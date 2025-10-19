package ru.yandex.practicum.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PostDto(
        @JsonProperty("id") long id,
        @JsonProperty("title") String title,
        @JsonProperty("text") String text,
        @JsonProperty("tags") String[] tags,
        //@JsonProperty("image") String image,
        @JsonProperty("likesCount") long likesCount,
        @JsonProperty("commentsCount") long commentsCount

) {

    @JsonCreator
    public PostDto(long id, String title, String text, String[] tags, long likesCount, long commentsCount) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.tags = tags;
        //this.image = image;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }
}