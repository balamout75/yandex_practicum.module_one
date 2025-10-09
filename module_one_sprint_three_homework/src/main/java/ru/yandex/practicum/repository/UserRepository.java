package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.User;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    void save(User user);

    void update(Long id, User user);

    void deleteById(Long id);
}
