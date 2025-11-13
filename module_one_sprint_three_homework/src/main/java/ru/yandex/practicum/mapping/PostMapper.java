package ru.yandex.practicum.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.dto.CommentDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.dto.PostDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);
    PostDto toPostDto(Post post); //map User to UserResponse
    List<PostDto> toPostDtoList(List<Post> posts);
    CommentDto toCommentDto(Comment comment); //map User to UserResponse
    List<CommentDto> toCommentDtoList(List<Comment> comments);//map list of User to list of UserResponse
}
