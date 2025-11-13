package ru.yandex.practicum.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ResponseDto (
    @JsonProperty("posts") List<PostDto> posts,
    @JsonProperty("hasPrev") boolean hasPrev,
    @JsonProperty("hasNext") boolean hasNext,
    @JsonProperty("lastPage") int lastPage
) {

    @JsonCreator

    public ResponceDto(List<PostDto> posts, boolean hasPrev, boolean hasNext, int lastPage) {
        this.posts = posts;
        this.hasPrev = hasPrev;
        this.hasNext = hasNext;
        this.lastPage = lastPage;
    }
}
