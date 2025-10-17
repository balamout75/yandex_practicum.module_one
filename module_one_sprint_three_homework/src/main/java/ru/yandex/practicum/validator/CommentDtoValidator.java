package ru.yandex.practicum.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import ru.yandex.practicum.DTO.CommentDTO;
import ru.yandex.practicum.DTO.PostDTO;

public class CommentDtoValidator implements SmartValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CommentDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CommentDTO commentDTO = (CommentDTO) target;
        if (commentDTO.text().isEmpty()) {
            errors.rejectValue("text", "text.required", "text cannot be empty");
        }
        if (commentDTO.postId()==0) {
            errors.rejectValue("postId", "postId.required", "postId cannot be zero");
        }
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        CommentDTO commentDTO = (CommentDTO) target;
        if (validationHints.length > 0) {
            if (validationHints[0] == "update") {
                if (commentDTO.id() == 0) {
                    errors.rejectValue("id", "id required", "CommentId cannot be zero");
                }
            }
        }
        this.validate(target, errors);
    }
}
