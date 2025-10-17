package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.DTO.CommentDTO;
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
        List<CommentDTO> commentDTOList = postMapper.toCommentDTOList(comments);
        return new ResponseEntity<>(commentDTOList.toArray(), HttpStatus.OK);
    }
    //1 comments getting
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable(name = "postid") Long postId,
                                            @PathVariable(name = "id") Long id) {
        if (!service.existsById(postId, id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't update comment");
        else
            return new ResponseEntity<>(postMapper.toCommentDTO(service.getById(id)),HttpStatus.OK);
    }

    //2 comments creation
    @PostMapping
    public ResponseEntity<?> saveComment(@PathVariable(name = "postid") Long postId,
                                         @RequestBody CommentDTO commentDTO) {
        Errors errors = new BeanPropertyBindingResult(commentDTO, "commentDTO");
        commentDtoValidator.validate(commentDTO, errors) ;
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }
        if (postId!=commentDTO.postId()) {
            return ResponseEntity.badRequest().body("Incorrect request");
        }
        return new ResponseEntity<>(postMapper.toCommentDTO(service.save(commentDTO)),
                                    HttpStatus.CREATED);
    }

    //3 update post
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "postid") Long postId,
                                    @PathVariable(name = "id") Long id,
                                    @RequestBody CommentDTO commentDTO) {
        Errors errors = new BeanPropertyBindingResult(commentDTO, "commentDTO");
        commentDtoValidator.validate(commentDTO, errors, "update") ;
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }
        if ((postId!=commentDTO.postId())||(id!=commentDTO.id())) {
            return ResponseEntity.badRequest().body("Incorrect request");
        }
        if (!service.existsById(postId, id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't update comment");
        else
            return new ResponseEntity<>(postMapper.toCommentDTO(service.update(id, commentDTO)),
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
