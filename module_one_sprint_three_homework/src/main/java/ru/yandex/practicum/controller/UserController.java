package ru.yandex.practicum.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> getUsers() {
        System.out.println("Вывели список");
        return service.findAll();
    }


    @RequestMapping(method = RequestMethod.POST)
    @PostMapping
    public void save(@RequestBody User user) {
        //public void save() {
        System.out.println("Post mapping");
        service.save(user);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable(name = "id") Long id, @RequestBody User user) {
        service.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        service.deleteById(id);
    }

}
