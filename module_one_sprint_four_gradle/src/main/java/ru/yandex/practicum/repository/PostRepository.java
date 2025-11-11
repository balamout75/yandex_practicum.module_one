package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.model.Post;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    Post findById(Long id);

    void deleteById(Long id);

    boolean existsById(Long id);

    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("select distinct p from Post p join p.tags t where lower(t.tag) in (:tags) group by p.id having count(p.id) = :tagCount")
    Page<Post> findByAllTags(List<String> tags, int tagCount, Pageable pageable);

    @Query("select distinct p from Post p join p.tags t where p.title ilike :searchSubString and lower(t.tag) in (:tags) group by p.id having count(p.id) = :tagCount")
    Page<Post> findBySearchStringAndAllTags(String searchSubString, List<String> tags, int tagCount, Pageable pageable);

    @Query(value = "SELECT NEXTVAL('image_sequence')", nativeQuery = true)
    Long getImageSuffix();


}
