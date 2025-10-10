package ru.yandex.practicum.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.DTO.CommentDTO;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.mapping.PostMapper;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService service;
    private final PostMapper postMapper=PostMapper.INSTANCE;

    public PostController(PostService service) {
        this.service = service;
    }

    //1 posts list returning
    @GetMapping
    public List<PostDTO> getAllPosts() {
        List<Post> posts = service.findAll();
        List<PostDTO> postDTOList = postMapper.toPostDTOList(posts);
        System.out.println("Вывели список постов");
        return postDTOList;
    }
    //2 post getting
    @GetMapping("/{id}")
    public PostDTO getPostById(@PathVariable(name = "id") Long id) {
        System.out.println("Вернули пост");
        //service.update(id, user);
        return null;
    }

    //3 post creation
    @RequestMapping(method = RequestMethod.POST)
    @PostMapping
    public PostDTO savePost(@RequestBody PostDTO postDTO) {
        //public void save() {
        System.out.println("Post creation");
        //service.save(user);
        return null;
    }

    //4 update post
    @PutMapping("/{id}")
    public PostDTO update(@PathVariable(name = "id") Long id, @RequestBody PostDTO postDTO) {
        //service.update(id, user);
        System.out.println("Post updating");
        return null;
    }

    //5 update post
    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        System.out.println("Post deleting");
        //service.deleteById(id);
    }

    //6 increment likes counter
    @PostMapping("/{id}/likes")
    public long like(@PathVariable(name = "id") Long id) {
        System.out.println("New like");
        //service.save(user);
        return 2L;
    }

    //7 upload post image
    @PutMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@PathVariable("id") Long id,
                                               @RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("empty file");
        }
        boolean ok = true;//service.uploadAvatar(id, file.getBytes());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed to upload image");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("ok");
    }

    //8 get post image
    @GetMapping(value = "/{id}/avatar", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getAvatar(@PathVariable("id") Long id) {
        /*if (!service.exists(id)) {
            return ResponseEntity.notFound().build();
        }*/

        byte[] bytes = new byte[]{(byte) 137, 80, 78, 71}; //byte[] bytes = service.getAvatar(id);
        if (bytes == null || bytes.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(bytes);
    }

    @GetMapping("/{id}/comments")
    public List<CommentDTO> getAllComments() {
        System.out.println("Вывели список комментов");
        //return service.findAll();
        return null;
    }

}
