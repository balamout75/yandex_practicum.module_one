package ru.yandex.practicum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.DTO.CommentDTO;
import ru.yandex.practicum.mapping.PostMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.service.CommentService;


import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/posts/{postid}/comments")
public class CommentController {

    private final CommentService service;
    private final PostMapper postMapper=PostMapper.INSTANCE;

    public CommentController(CommentService service) {
        this.service = service;
    }

    //0 comments list returning
    @GetMapping
    public ResponseEntity<?> getAllPostsComments(@PathVariable(name = "postid") Long postid) {
        List<Comment> comments = service.findAll(postid);
        List<CommentDTO> commentDTOList = postMapper.toCommentDTOList(comments);
        System.out.println("Вывели список комментов");
        return new ResponseEntity<>(commentDTOList.toArray(), HttpStatus.OK);
    }
    //1 comments getting
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable(name = "id") Long id) {
        System.out.println("Вернули коммент");
        return new ResponseEntity<>(postMapper.toCommentDTO(service.getById(id)), HttpStatus.OK);
    }

    //2 comments creation
    @PostMapping
    public ResponseEntity<?> saveComment(@RequestBody CommentDTO commentDTO) {
        System.out.println("Comment creation");
        return new ResponseEntity<>(postMapper.toCommentDTO(service.save(commentDTO)), HttpStatus.OK);
    }

    //3 update post
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id, @RequestBody CommentDTO commentDTO) {
        System.out.println("Comment updating");
        return new ResponseEntity<>(postMapper.toCommentDTO(service.update(id, commentDTO)), HttpStatus.OK);
    }

    //4 comments deleting
    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        System.out.println("Comment deleting");
        //service.deleteById(id);
    }
}
