package ru.yandex.practicum.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.DTO.PostDTO;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);
    PostDTO toPostDTO(Post post); //map User to UserResponse
    List<PostDTO> toPostDTOList(List<Post> posts); //map list of User to list of UserResponse
}
