package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.PostRepository;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public void save(PostDTO postDTO) {
        postRepository.save(postDTO);
    }

    public void update(Long id, PostDTO postDTO) {
        postRepository.update(id, postDTO);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }
}
