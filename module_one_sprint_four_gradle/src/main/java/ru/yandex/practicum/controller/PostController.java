package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
//import ru.yandex.practicum.dto.CommentDto;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.dto.ResponceDto;

import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.PostService;
import ru.yandex.practicum.validator.PostDtoValidator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.ArrayList;
import java.util.List;



@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostDtoValidator postDtoValidator;
    private final PostService service;
    //private final PostMapper postMapper=PostMapper.INSTANCE;

    public PostController(PostService service, PostDtoValidator postDtoValidator) {
        this.service = service;
        this.postDtoValidator = postDtoValidator;
    }

    //1 posts list returning
    @GetMapping()
    public ResponseEntity<ResponceDto> getAllPosts(@RequestParam("search") String search,
                                         @RequestParam("pageNumber") int pageNumber,
                                         @RequestParam("pageSize") int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        Page<PostDto> pagedPost = service.findAll(search, pageable);
        return new ResponseEntity<>(new ResponceDto(pagedPost.toList(),
                                                        pagedPost.hasPrevious(),
                                                        pagedPost.hasNext(),
                                                        pagedPost.getTotalPages()),
                                            HttpStatus.OK);
    }

    //2 post getting
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable(name = "id") Long id) {
        PostDto postDto = service.getById(id);
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }

    //3 post creation
    @PostMapping
    public ResponseEntity<?> savePost(@RequestBody PostDto postDto) {
        Errors errors = new BeanPropertyBindingResult(postDto, "postDto");
        postDtoValidator.validate(postDto, errors) ;
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }
        return new ResponseEntity<>(service.save(postDto),
                                    HttpStatus.CREATED);
    }

    //4 update post
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id,
                                    @RequestBody PostDto postDto) {
        Errors errors = new BeanPropertyBindingResult(postDto, "postDto");
        postDtoValidator.validate(postDto, errors, "update") ;
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }
        if (id!=postDto.id()) {
            return ResponseEntity.badRequest().body("Incorrect request");
        }
        if (!service.exists(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't update comment");
        else
            return new ResponseEntity<>(null,HttpStatus.ACCEPTED);
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
    public ResponseEntity<?> like(@PathVariable(name = "id") Long id) {
        if (!service.exists(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't locate post");
        else {
            return new ResponseEntity<>(service.like(id), HttpStatus.OK);
        }
    }

    //7 upload post image
    @PutMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@PathVariable("id") Long id,
                                              @RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file");
        }
        if (!service.exists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post not found");
        }
        if (service.uploadImage(id, file))
            return ResponseEntity.ok().body("Image uploaded");
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something going wrong");

    }

    //8 get post image
    @GetMapping(value = "/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable("id") Long id) {
        System.out.println("Да все вроде хорошо");
        Resource file = service.getImage(id);
        if (file == null) {
                return ResponseEntity.notFound().build();
        } else return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(file);
    }

    //9 get post image
    @GetMapping(value = "/undefined/comments")
    public ResponseEntity<?> getStub() {
        //ArrayList<CommentDto> stub = new ArrayList<>();
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

}
