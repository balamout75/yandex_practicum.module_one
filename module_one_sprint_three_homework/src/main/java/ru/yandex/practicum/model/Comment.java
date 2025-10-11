package ru.yandex.practicum.model;

public class Comment {

    long id;
    String text;
    long postid;

    public Comment() {

    }

    public Comment(long id, String text, long postid) {
        this.id = id;
        this.text = text;
        this.postid = postid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getPostid() {
        return postid;
    }

    public void setPostid(long postid) {
        this.postid = postid;
    }
}
