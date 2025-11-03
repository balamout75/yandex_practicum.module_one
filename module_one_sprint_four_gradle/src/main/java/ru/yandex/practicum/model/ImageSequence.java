package ru.yandex.practicum.model;

import jakarta.persistence.*;

@Entity
public class ImageSequence {
    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fileSuffixGenerator")
    //@SequenceGenerator(name = "fileSuffixGenerator", sequenceName = "image_sequence", allocationSize = 1)
    private Long id;

}
