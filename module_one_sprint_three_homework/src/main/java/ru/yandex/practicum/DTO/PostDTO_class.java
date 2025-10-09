package ru.yandex.practicum.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostDTO_class {
    @JsonProperty("id") long id;
    @JsonProperty("title") String title;
    @JsonProperty("text") String text;
    @JsonProperty("tags") String[] tags;
    @JsonProperty("likesCount") long likesCount;
    @JsonProperty("commentsCount") long commentsCount;

    public PostDTO_class() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }
}
