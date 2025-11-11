package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PostsandtagId implements Serializable {
    private static final long serialVersionUID = 538602890478923447L;
    @Column(name = "POST", nullable = false)
    private Long post;

    @Column(name = "TAG", nullable = false)
    private Long tag;

    public Long getPost() {
        return post;
    }

    public void setPost(Long post) {
        this.post = post;
    }

    public Long getTag() {
        return tag;
    }

    public void setTag(Long tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PostsandtagId entity = (PostsandtagId) o;
        return Objects.equals(this.post, entity.post) &&
                Objects.equals(this.tag, entity.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(post, tag);
    }

}