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
import java.util.Map;
import java.util.Optional;
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
        System.out.println("строка поиска '"+search+"'");
        String taggedsearch=search.replace("#"," #"); //.trim();
        //System.out.println("строка поиска '"+taggedsearch+"'");
        String [] words=taggedsearch.split("\\s+");
        //String [] words=search.split(" ");

        System.out.println("слов в строке"+(words.length));
        Map<Boolean, List<String>> partitioned = Stream.of(words)
                    .filter(n -> !n.isBlank())
                    .collect(Collectors.partitioningBy(n -> n.charAt(0) == '#'));
        List<String> searchwords = partitioned.get(false);
        List<String> tags = partitioned.get(true).stream()
                    .map(s -> s.substring(1))
                    .filter(n -> !n.isBlank())
                    .toList();
        System.out.println("Компоненты поиска (" + searchwords.size() + ")");
        searchwords.forEach(System.out::println);
        System.out.println("тэги (" + tags.size() + ")");
        tags.forEach(System.out::println);
        return postRepository.findAll(searchwords, tags, pageNumber, pageSize);
    }

    public Post save(PostDTO postDTO) {
        return postRepository.save(postDTO);
    }

    public Post update(Long id, PostDTO postDTO) {
        return postRepository.update(id, postDTO);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    public Post getById(Long id) {
        return postRepository.getById(id);
    }

    public Resource getImage(Long id)  {
        try {
            String fileName=postRepository.getFileNameByPostId(id);
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
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
            String fileName=file.getOriginalFilename().replace(".","_"+postRepository.getFileSuffix()+".");
            System.out.println("сохранияем файл "+ fileName);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(fileName);

            System.out.println("полное имя "+ fileName);
            file.transferTo(filePath);
            return postRepository.setFileNameByPostId(id,fileName);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Long like(Long id) { return postRepository.like(id); }
}
