package ru.yandex.practicum.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "POSTSANDTAGS")
public class Postsandtag {
    @EmbeddedId
    private PostsandtagId id;

    @MapsId("post")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "POST", nullable = false)
    private Post post;

    @MapsId("tag")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "TAG", nullable = false)
    private Tag tag;

    public PostsandtagId getId() {
        return id;
    }

    public void setId(PostsandtagId id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

}