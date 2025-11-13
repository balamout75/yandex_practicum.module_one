package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.mapping.PostDtoMapper;
import ru.yandex.practicum.mapping.PostEntityMapper;
import ru.yandex.practicum.mapping.TagSearcher;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.Tag;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.repository.TagRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostService {

    @Value("${images.path}")
    private String UPLOAD_DIR;

    private final PostRepository postRepository;
    //private final ImageSequenceRepository imageSequenceRepository;

    private final PostEntityMapper postEntityMapper;
    private final PostDtoMapper postDtoMapper;

    public PostService(PostRepository postRepository, TagRepository tagRepository, TagSearcher tagSearcher) {
        postEntityMapper = new PostEntityMapper();
        postDtoMapper = new PostDtoMapper(tagSearcher);
        this.postRepository = postRepository;


    }

    public Page<PostDto> findAll(String search, Pageable pageable) {
        int searchCondition=1;
        //1 есть подстрока поиска и тэги
        //2 только подстрока
        //3 только тэги
        //4 ищем все
        String taggedsearch=search.replace("#"," #"); //.trim();
        String [] words=taggedsearch.split("\\s+");

        Map<Boolean, List<String>> partitioned = Stream.of(words)
                    .filter(n -> !n.isBlank())
                    .map(String::toLowerCase)
                    .collect(Collectors.partitioningBy(n -> n.charAt(0) == '#'));

        List<String> tags = partitioned.get(true).stream()
                    .map(s -> s.substring(1))
                    .filter(n -> !n.isBlank())
                    .map(String::toLowerCase)
                    .distinct()
                    .sorted()
                    .toList();

        if (tags.isEmpty()) searchCondition++;

        String searchSubString = partitioned.get(false).stream()
                .map(String::toLowerCase)
                .reduce((a, b) -> a + " " + b)
                .orElse("");

        if (searchSubString.isEmpty()) searchCondition=searchCondition+2;

        Page<Post>  postEntities = switch (searchCondition) {
            case 1 -> postRepository.findBySearchStringAndAllTags("%"+searchSubString+"%", tags, tags.size(), pageable);
            case 2 -> postRepository.findByTitleContainingIgnoreCase(searchSubString,pageable);
            case 3 -> postRepository.findByAllTags(tags,tags.size(),pageable);
            default -> postRepository.findAll(pageable);
        };
        return postEntities.map(postEntityMapper::toDtoWithTitleForming);
    }

    public PostDto getById(Long id) {
        return postEntityMapper.toDto(postRepository.findById(id));
    }

    public PostDto save(PostDto postDto) {
        Post post=new Post();
        post.setImage("");
        post.setLikesCount(0L);
        return this.save(postDto, post);
    }

    public PostDto update(PostDto postDto) {
        Post post = postRepository.findById(postDto.id());
        return this.save(postDto, post);
    }

    private PostDto save(PostDto postDto, Post originalPost) {

        Post post= postDtoMapper.toEntity(postDto, originalPost);
        return postEntityMapper.toDto(postRepository.save(post)); //pem.toDto(postRepository.save(postDto));
    }


    public Long like(Long id) {
        Post post=postRepository.findById(id);
        post.setLikesCount(post.getLikesCount()+1);
        postRepository.save(post);
        Post resultPost = postRepository.save(post);
        return resultPost.getLikesCount(); ///postRepository.like(id);
    }

    public boolean exists(Long id) {
        return postRepository.existsById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    public Resource getImage(Long id)  {
        try {
            Post post = postRepository.findById(id);
            String fileName=Optional.ofNullable(post.getImage())
                                .orElse("");
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
            String fileName=file.getOriginalFilename().replace(".","_"+postRepository.getImageSuffix()+".");
            //String fileName=file.getOriginalFilename();
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(fileName);
            file.transferTo(filePath);
            Post post = postRepository.findById(id);
            post.setImage(fileName);
            postRepository.save(post);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
