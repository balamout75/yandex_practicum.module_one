package ru.yandex.practicum.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import ru.yandex.practicum.DTO.PostDTO;

public class PostDtoValidator implements SmartValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PostDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PostDTO postDTO = (PostDTO) target;
        if (postDTO.title().isEmpty()) {
            errors.rejectValue("title", "title.required", "Title cannot be empty");
        }
        if (postDTO.text().isEmpty()) {
            errors.rejectValue("text", "text.required", "Post cannot be empty");
        }
        if (postDTO.tags().length == 0) {
            errors.rejectValue("tags", "tags.required", "You should specify tags");
        }
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        PostDTO postDTO = (PostDTO) target;
        if (validationHints.length > 0) {
            if (validationHints[0] == "update") {
                if (postDTO.id() == 0) {
                    errors.rejectValue("id", "id required", "Id cannot be empty");
                }
            }
        }
        this.validate(target, errors);
    }
}
