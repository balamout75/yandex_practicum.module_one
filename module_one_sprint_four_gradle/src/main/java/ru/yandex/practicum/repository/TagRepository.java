package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Tag;

import java.util.List;
import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag, Integer> {

    Optional<Tag> findByTag(String tag);
}
