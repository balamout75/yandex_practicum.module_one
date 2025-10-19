package ru.yandex.practicum.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import ru.yandex.practicum.DTO.CommentDto;
import ru.yandex.practicum.DTO.PostDto;

public class CommentDtoValidator implements SmartValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CommentDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CommentDto commentDto = (CommentDto) target;
        if (commentDto.text().isEmpty()) {
            errors.rejectValue("text", "text.required", "text cannot be empty");
        }
        if (commentDto.postId()==0) {
            errors.rejectValue("postId", "postId.required", "postId cannot be zero");
        }
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        CommentDto commentDto = (CommentDto) target;
        if (validationHints.length > 0) {
            if (validationHints[0] == "update") {
                if (commentDto.id() == 0) {
                    errors.rejectValue("id", "id required", "CommentId cannot be zero");
                }
            }
        }
        this.validate(target, errors);
    }
}
