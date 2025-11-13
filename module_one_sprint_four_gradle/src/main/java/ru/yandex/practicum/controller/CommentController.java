package ru.yandex.practicum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CommentDto;

import ru.yandex.practicum.service.CommentService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/posts/{postid}/comments")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    //1 comments list returning
    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllPostsComments(@PathVariable(name = "postid") Long postId) {
        return new ResponseEntity<>(service.findAll(postId), HttpStatus.OK);
    }
    //2 comments getting
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable(name = "postid") Long postId,
                                                     @PathVariable(name = "id") Long id) {
        if (!service.existsById(postId, id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        else
            return new ResponseEntity<>(service.getById(id),HttpStatus.OK);
    }

    //3 comments creation
    @PostMapping
    public ResponseEntity<?> saveComment(@PathVariable(name = "postid") Long postId,
                                         @Validated(CommentDto.New.class) @RequestBody CommentDto commentDto) {
        if (!postId.equals(commentDto.postId())) {
            return ResponseEntity.badRequest().body("Can't save comment");
        }
        return new ResponseEntity<>(service.save(commentDto),
                                    HttpStatus.CREATED);
    }

    //4 update post
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "postid") Long postId,
                                    @PathVariable(name = "id") Long id,
                                    @Validated(CommentDto.Exist.class) @RequestBody CommentDto commentDto) {
        if ((!postId.equals(commentDto.postId()))||(!id.equals(commentDto.id()))) {
            return ResponseEntity.badRequest().body("Incorrect request");
        }
        if (!service.existsById(postId, id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't update comment");
        else
            return new ResponseEntity<>(service.update(commentDto),
                                        HttpStatus.ACCEPTED);
    }

    //5 comments deleting
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable(name = "postid") Long postId, @PathVariable(name = "id") Long commentId) {
        if (!service.existsById(postId, commentId)) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't delete comment");
        else {
            service.deleteById(postId,commentId);
            return ResponseEntity.status(HttpStatus.OK).body("Comment deleted");
        }
    }
}
