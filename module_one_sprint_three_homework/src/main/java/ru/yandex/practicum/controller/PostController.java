package ru.yandex.practicum.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.DTO.CommentDTO;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.DTO.ResponceDTO;
import ru.yandex.practicum.mapping.PostMapper;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.PostService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@CrossOrigin
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService service;
    private final PostMapper postMapper=PostMapper.INSTANCE;

    public PostController(PostService service) {
        this.service = service;
    }

    //1 posts list returning

    @GetMapping()
    public ResponseEntity<?> getAllPosts(@RequestParam("search") String search,
                                         @RequestParam("pageNumber") int pageNumber,
                                         @RequestParam("pageSize") int pageSize) {
        System.out.println("Вывели список постов "+search+" "+pageNumber+" "+pageSize);

        List<Post> posts = service.findAll(search, pageNumber, pageSize);
        List<PostDTO> postDTOList = postMapper.toPostDTOList(posts);
        long total_count= Optional.of(posts.getFirst().getTotal_records()).orElse(0L);

        System.out.println("Записей "+total_count+" текущая страница "+pageNumber+" записей на странице "+pageSize);

        boolean hasPrev=pageNumber>1; System.out.println("hasPrev "+hasPrev);
        boolean hasNext=((long) pageNumber *pageSize)<total_count; System.out.println("hasNext "+hasNext);
        System.out.println("последняя страница "+(int) Math.ceil((double) total_count / pageSize));
        return new ResponseEntity<>(new ResponceDTO(postDTOList,
                                        pageNumber>1,
                                        ((long) pageNumber * pageSize)<total_count,
                                        (int) Math.ceil((double) total_count / pageSize)),
                                    HttpStatus.OK);
    }

    //2 post getting
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable(name = "id") Long id) {
        System.out.println("Вернули пост");
        return new ResponseEntity<>(postMapper.toPostDTO(service.getById(id)), HttpStatus.OK);
    }

    //3 post creation

    @PostMapping
    public ResponseEntity<?> savePost(@RequestBody PostDTO postDTO) {
        System.out.println("Post creation");
        return new ResponseEntity<>(postMapper.toPostDTO(service.save(postDTO)), HttpStatus.CREATED);
    }

    //4 update post
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id, @RequestBody PostDTO postDTO) {
        //service.update(id, user);
        System.out.println("Post updating");
        return new ResponseEntity<>(postMapper.toPostDTO(service.update(id, postDTO)), HttpStatus.OK);
    }

    //5 update post
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable(name = "id") Long id) {
        if (service.deleteById(id)) return ResponseEntity.status(HttpStatus.OK).body("Record deleted");
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Can't delete record");
    }

    //6 increment likes counter
    @PostMapping("/{id}/likes")
    public ResponseEntity<Long> like(@PathVariable(name = "id") Long id) {
        System.out.println("New like");
        Long likecounter = service.like(id);
        return new ResponseEntity<>(likecounter, HttpStatus.OK);
    }

    //7 upload post image
    @PutMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> uploadImage(@PathVariable("id") Long id,
                                                @RequestParam("image") MultipartFile file) throws Exception {

        System.out.println("файл сохраняем");
        if (file.isEmpty()) {
            System.out.println("файл пустой");
            return ResponseEntity.badRequest()
                    .body(null);
        }
        boolean ok = service.uploadImage(id, file);
        if (!ok) {
            System.out.println("файл не сохранили");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);

        }
        System.out.println("Да все вроде хорошо");
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(service.getImage(id));
    }

    //8 get post image
    @GetMapping(value = "/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable("id") Long id) {
        Resource file = service.getImage(id);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(file);
    }

    @GetMapping(value = "/undefined/comments")
    public ResponseEntity<?> getStub() {
        ArrayList<CommentDTO> stub = new ArrayList<CommentDTO>();
        return new ResponseEntity<>(stub, HttpStatus.OK);
    }

}
