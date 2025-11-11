package ru.yandex.practicum.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import ru.yandex.practicum.DTO.PostDto;

public class PostDtoValidator implements SmartValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PostDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PostDto postDto = (PostDto) target;
        if (postDto.title().isEmpty()) {
            errors.rejectValue("title", "title.required", "Title cannot be empty");
        }
        if (postDto.text().isEmpty()) {
            errors.rejectValue("text", "text.required", "Post cannot be empty");
        }
        if (postDto.tags().length == 0) {
            errors.rejectValue("tags", "tags.required", "You should specify tags");
        }
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        PostDto postDto = (PostDto) target;
        if (validationHints.length > 0) {
            if (validationHints[0] == "update") {
                if (postDto.id() == 0) {
                    errors.rejectValue("id", "id required", "Id cannot be empty");
                }
            }
        }
        this.validate(target, errors);
    }
}
