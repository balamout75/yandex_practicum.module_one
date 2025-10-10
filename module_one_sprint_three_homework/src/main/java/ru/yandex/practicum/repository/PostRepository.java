package ru.yandex.practicum.repository;

import org.springframework.core.io.Resource;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.User;

import java.util.List;

public interface PostRepository {

    List<Post> findAll();

    void save(PostDTO user);

    void update(Long id, PostDTO postDTO);

    void deleteById(Long id);

    Post getById(Long id);

    Resource getImageById(Long id);
}
