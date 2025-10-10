package ru.yandex.practicum.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.PostRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PostService {

    public static final String UPLOAD_DIR = "uploads/";

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post save(PostDTO postDTO) {
        return postRepository.save(postDTO);
    }

    public void update(Long id, PostDTO postDTO) {
        postRepository.update(id, postDTO);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    public Post getById(Long id) {
        return postRepository.getById(id);
    }

    public Resource getImage(Long id)  {
        Path uploadDir = Paths.get(UPLOAD_DIR);
        /*Path path = uploadDir.resolve("my.png");
        System.out.println("путь к файлу "+ path);
        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);

            return null;
        } catch (IOException e) {
            System.err.println("already exists: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }*/

        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve("Peschannaya.png").normalize();
            System.out.println("путь к файлу "+ filePath);
            byte[] content = Files.readAllBytes(filePath);
            return new ByteArrayResource(content);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    public boolean uploadImage(Long id, MultipartFile file) {
        try {
            Path uploadDir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(file.getOriginalFilename());
            file.transferTo(filePath);

            return true;
        } catch (IOException e) {
            //throw new RuntimeException(e.getMessage(), e);
            return false;
        }
    }
}
