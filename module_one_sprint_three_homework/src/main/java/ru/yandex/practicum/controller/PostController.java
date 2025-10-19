package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.DTO.CommentDTO;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.DTO.ResponceDTO;
import ru.yandex.practicum.mapping.PostMapper;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.PostService;
import ru.yandex.practicum.validator.PostDtoValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostDtoValidator postDtoValidator;
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
        List<Post> posts = service.findAll(search, pageNumber, pageSize);

        List<PostDTO> postDTOList = postMapper.toPostDTOList(posts);
        long total_count= Optional.ofNullable(posts.getFirst())
                                            .map(Post::getTotal_records)
                                            .orElse(0L);
        return new ResponseEntity<>(new ResponceDTO(postDTOList,
                                        pageNumber>1,
                                        ((long) pageNumber * pageSize)<total_count,
                                        (int) Math.ceil((double) total_count / pageSize)),
                                    HttpStatus.OK);
    }

    //2 post getting
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(postMapper.toPostDTO(service.getById(id)), HttpStatus.OK);
    }

    //3 post creation
    @PostMapping
    public ResponseEntity<?> savePost(@RequestBody PostDTO postDTO) {
        Errors errors = new BeanPropertyBindingResult(postDTO, "postDTO");
        postDtoValidator.validate(postDTO, errors) ;
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }
        return new ResponseEntity<>(postMapper.toPostDTO(service.save(postDTO)),
                                    HttpStatus.CREATED);
    }

    //4 update post
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id,
                                    @RequestBody PostDTO postDTO) {
        Errors errors = new BeanPropertyBindingResult(postDTO, "postDTO");
        postDtoValidator.validate(postDTO, errors, "update") ;
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }
        if (id!=postDTO.id()) {
            return ResponseEntity.badRequest().body("Incorrect request");
        }
        if (!service.exists(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't update comment");
        else
            return new ResponseEntity<>(postMapper.toPostDTO(service.update(id, postDTO)),HttpStatus.ACCEPTED);
    }

    //5 update post
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable(name = "id") Long id) {
        if (!service.exists(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't update comment");
        else {
            service.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Record deleted");
        }
    }

    //6 increment likes counter
    @PostMapping("/{id}/likes")
    public ResponseEntity<Long> like(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(service.like(id), HttpStatus.OK);
    }

    //7 upload post image
    @PutMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@PathVariable("id") Long id,
                                              @RequestParam("image") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file");
        }
        if (!service.exists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post not found");
        }
        boolean ok = service.uploadImage(id, file);
        if (service.uploadImage(id, file))
            return ResponseEntity.ok().body("Image uploaded");
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something going wrong");

    }

    //8 get post image
    @GetMapping(value = "/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable("id") Long id) {
        System.out.println("Да все вроде хорошо");
        Resource file=null;
        file = service.getImage(id);
        if (file == null) {
                return ResponseEntity.notFound().build();
        } else return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(file);
    }

    @GetMapping(value = "/undefined/comments")
    public ResponseEntity<?> getStub() {
        ArrayList<CommentDTO> stub = new ArrayList<CommentDTO>();
        return new ResponseEntity<>(stub, HttpStatus.OK);
    }

}
