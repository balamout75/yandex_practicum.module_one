package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.DTO.CommentDto;
import ru.yandex.practicum.mapping.PostMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.validator.CommentDtoValidator;


import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/posts/{postid}/comments")
public class CommentController {

    @Autowired
    private CommentDtoValidator commentDtoValidator;
    private final CommentService service;
    private final PostMapper postMapper=PostMapper.INSTANCE;

    public CommentController(CommentService service) {
        this.service = service;
    }

    //0 comments list returning
    @GetMapping
    public ResponseEntity<?> getAllPostsComments(@PathVariable(name = "postid") Long postId) {
        List<Comment> comments = service.findAll(postId);
        List<CommentDto> commentDtoList = postMapper.toCommentDtoList(comments);
        return new ResponseEntity<>(commentDtoList.toArray(), HttpStatus.OK);
    }
    //1 comments getting
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable(name = "postid") Long postId,
                                            @PathVariable(name = "id") Long id) {
        if (!service.existsById(postId, id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't update comment");
        else
            return new ResponseEntity<>(postMapper.toCommentDto(service.getById(id)),HttpStatus.OK);
    }

    //2 comments creation
    @PostMapping
    public ResponseEntity<?> saveComment(@PathVariable(name = "postid") Long postId,
                                         @RequestBody CommentDto commentDto) {
        Errors errors = new BeanPropertyBindingResult(commentDto, "commentDto");
        commentDtoValidator.validate(commentDto, errors) ;
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }
        if (postId!=commentDto.postId()) {
            return ResponseEntity.badRequest().body("Incorrect request");
        }
        return new ResponseEntity<>(postMapper.toCommentDto(service.save(commentDto)),
                                    HttpStatus.CREATED);
    }

    //3 update post
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "postid") Long postId,
                                    @PathVariable(name = "id") Long id,
                                    @RequestBody CommentDto commentDto) {
        Errors errors = new BeanPropertyBindingResult(commentDto, "commentDto");
        commentDtoValidator.validate(commentDto, errors, "update") ;
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }
        if ((postId!=commentDto.postId())||(id!=commentDto.id())) {
            return ResponseEntity.badRequest().body("Incorrect request");
        }
        if (!service.existsById(postId, id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't update comment");
        else
            return new ResponseEntity<>(postMapper.toCommentDto(service.update(id, commentDto)),
                                        HttpStatus.ACCEPTED);
    }

    //4 comments deleting
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "postid") Long postId, @PathVariable(name = "id") Long id) {
        if (!service.existsById(postId, id)) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't delete comment");
        else {
            service.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Comment deleted");
        }
    }
}
