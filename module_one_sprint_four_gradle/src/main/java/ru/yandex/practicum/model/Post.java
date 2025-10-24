package ru.yandex.practicum.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "POSTS")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Lob
    @Column(name = "TITLE", nullable = false)
    private String title;

    @Lob
    @Column(name = "TEXT", nullable = false)
    private String text;

    @ColumnDefault("''")
    @Lob
    @Column(name = "IMAGE", nullable = false)
    private String image;

    @ColumnDefault("0")
    @Column(name = "LIKESCOUNT")
    private Long likescount;

    @OneToMany(mappedBy = "postid")
    private Set<Comment> comments = new LinkedHashSet<>();

    @ManyToMany
    private Set<Tag> tags = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getLikescount() {
        return likescount;
    }

    public void setLikescount(Long likescount) {
        this.likescount = likescount;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

}