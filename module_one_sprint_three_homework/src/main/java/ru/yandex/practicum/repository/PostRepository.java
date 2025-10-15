package ru.yandex.practicum.repository;

import org.springframework.core.io.Resource;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.User;

import java.util.List;

public interface PostRepository {

    //List<Post> findAll();

    Post save(PostDTO user);

    Post update(Long id, PostDTO postDTO);

    boolean  deleteById(Long id);

    Post getById(Long id);

    String getFileNameByPostId(Long id);

    boolean setFileNameByPostId(Long id, String fileName);

    List<String> getTagsByPostId(Long id);

    Long like(Long id);

    Long getPostsCommentsCountById(Long id);

    String getFileSuffix();

    List<Post> findAll(List<String> searchwords, List<String> tags, int pageNumber, int pageSize);

    boolean existsById(Long id);
}
