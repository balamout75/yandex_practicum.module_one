package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    //Page<Post> findByTitleLike(String title, Pageable pageable);

    Comment findById(Long id);

    //Page<Post> findByTitleLike(String title, Pageable pageable);


    /*
    Post update(Long id, PostDto postDto);

    void deleteById(Long id);

    Post getById(Long id);

    String getFileNameByPostId(Long id);

    void setFileNameByPostId(Long id, String fileName);

    List<String> getTagsByPostId(Long id);

    Long like(Long id);

    Long getPostsCommentsCountById(Long id);

    String getFileSuffix();

    List<Post> findAll(List<String> searchwords, List<String> tags, int pageNumber, int pageSize);

    boolean existsById(Long id);*/
}
