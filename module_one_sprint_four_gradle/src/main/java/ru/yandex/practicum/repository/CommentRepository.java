package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByPost(Post post);

    Comment findById(Long id);

    void deleteById(Long id);

    boolean existsByPost_IdAndId(Long postId, Long Id);

}
