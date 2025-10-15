package ru.yandex.practicum.controller; // Класс находится в пакете с контроллерами

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.DTO.ResponceDTO;
import ru.yandex.practicum.mapping.PostMapper;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.PostService;

import java.util.List;
import java.util.Optional;

@Controller // Указываем Spring-у, что этот компонент является контроллером
@RequestMapping("/api/posts")
public class HomeController {
    private final PostService service;
    private final PostMapper postMapper=PostMapper.INSTANCE;
    public HomeController(PostService service) {
        this.service = service;
    }

    /*@GetMapping("/home") // Принимаем GET-запрос по адресу /home
    @ResponseBody        // Указываем, что возвращаемое значение является ответом
    public String homePage() {
        return "<h1>Hello, world!</h1>"; // Ответ
    }*/

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
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

}
