package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.ImageSequence;

@Repository
public interface ImageSequenceRepository extends JpaRepository<ImageSequence, Integer> {

    @Query(value = "SELECT NEXTVAL('image_sequence')", nativeQuery = true)
    Long getImageSuffix();
}

