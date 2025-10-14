package ru.yandex.practicum.model;

public class Post {

    long id;
    String title;
    String text;
    String[] tags;
    String image;
    long likesCount;
    long commentsCount;
    long total_records;

    public Post() {

    }

    public Post(long id, String title, String text, String[] tags, String image, long likesCount, long commentsCount, long total_records) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.tags = tags;
        this.image = image;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.total_records = total_records;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public long getTotal_records() {
        return total_records;
    }

    public void setTotal_records(long total_records) {
        this.total_records = total_records;
    }
}
