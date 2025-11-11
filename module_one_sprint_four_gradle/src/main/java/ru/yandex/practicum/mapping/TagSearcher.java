package ru.yandex.practicum.mapping;

import ru.yandex.practicum.model.Tag;
import ru.yandex.practicum.repository.TagRepository;

public class TagSearcher {
    private final TagRepository tagRepository;

    public TagSearcher(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag searchTag(String tagBody) {
        return tagRepository.findByTagIgnoreCase(tagBody)
                .orElseGet(() -> tagRepository.save(new Tag(tagBody.toLowerCase())));
    }
}
