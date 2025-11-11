package ru.yandex.practicum.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.dto.ResponseDto;

import ru.yandex.practicum.service.PostService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    //1 posts list returning
    @GetMapping()
    public ResponseEntity<ResponseDto> getAllPosts(@RequestParam("search") String search,
                                                   @RequestParam("pageNumber") int pageNumber,
                                                   @RequestParam("pageSize") int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        Page<PostDto> pagedPost = service.findAll(search, pageable);
        return new ResponseEntity<>(new ResponseDto(pagedPost.toList(),
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
    public ResponseEntity<PostDto> savePost(@Validated(PostDto.New.class) @RequestBody PostDto postDto) {
        return new ResponseEntity<>(service.save(postDto),
                                    HttpStatus.CREATED);
    }

    //4 update post
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id,
                                    @Validated(PostDto.Exist.class) @RequestBody PostDto postDto) {
        if (!Objects.equals(id, postDto.id())) {
            return ResponseEntity.badRequest().body("Incorrect id");
        }
        if (!service.exists(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incorrect id");
        else
            return new ResponseEntity<>(service.update(postDto),HttpStatus.ACCEPTED);
    }

    //5 delete post
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
        Resource file = service.getImage(id);
        if (file == null) {
                return ResponseEntity.notFound().build();
        } else return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(file);
    }

    //9 frontend incorrect request stab
    @GetMapping(value = "/undefined/comments")
    public ResponseEntity<List<PostDto>> getStub() {
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

}
