package ru.yandex.practicum.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.descriptor.jdbc.NVarcharJdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "POSTS")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "TEXT", nullable = false)
    private String text;

    @ColumnDefault("''")
    @Column(name = "IMAGE", nullable = false)
    private String image;

    @ColumnDefault("0")
    @Column(name = "LIKESCOUNT")
    private Long likesCount;

    @OneToMany(mappedBy = "post")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Comment> comments = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
            name = "postsandtags",
            joinColumns = @JoinColumn(name = "post"),
            inverseJoinColumns = @JoinColumn(name = "tag"))
    private Set<Tag> tags = new LinkedHashSet<>();

    public Post() {
        this.image="";
        this.likesCount=0L;
    }


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

    public Long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Long likescount) {
        this.likesCount = likescount;
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