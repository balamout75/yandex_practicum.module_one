package ru.yandex.practicum.repository;

import ru.yandex.practicum.DTO.PostDto;
import ru.yandex.practicum.model.Post;

import java.util.List;

public interface PostRepository {

    Post save(PostDto user);

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

    boolean existsById(Long id);
}
