package ru.yandex.practicum.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.DTO.PostDto;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostService {

    public static final String UPLOAD_DIR = "uploads/";

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAll(String search, int pageNumber, int pageSize) {
        String taggedsearch=search.replace("#"," #"); //.trim();

        String [] words=taggedsearch.split("\\s+");

        Map<Boolean, List<String>> partitioned = Stream.of(words)
                    .filter(n -> !n.isBlank())
                    .map(String::toLowerCase)
                    .collect(Collectors.partitioningBy(n -> n.charAt(0) == '#'));
        List<String> searchwords = partitioned.get(false);
        List<String> tags = partitioned.get(true).stream()
                    .map(s -> s.substring(1))
                    .filter(n -> !n.isBlank())
                    .toList();
        return postRepository.findAll(searchwords, tags, pageNumber, pageSize);
    }

    public Post save(PostDto postDto) {
        return postRepository.save(postDto);
    }

    public Post update(Long id, PostDto postDto) {
        return postRepository.update(id, postDto);
    }

    public void deleteById(Long id) { postRepository.deleteById(id); }

    public Post getById(Long id) {
        return postRepository.getById(id);
    }

    public Resource getImage(Long id)  {
        try {
            String fileName=postRepository.getFileNameByPostId(id);
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
            byte[] content = Files.readAllBytes(filePath);
            return new ByteArrayResource(content);
        } catch (Exception e) {
            return null;
        }

    }

    public boolean uploadImage(Long id, MultipartFile file) {
        try {
            Path uploadDir = Paths.get(UPLOAD_DIR);
            String fileName=file.getOriginalFilename().replace(".","_"+postRepository.getFileSuffix()+".");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(fileName);
            file.transferTo(filePath);
            postRepository.setFileNameByPostId(id,fileName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long like(Long id) { return postRepository.like(id); }

    public boolean exists(Long id) { return postRepository.existsById(id); }

}
