package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.post.PostDto;
import com.roomster.roomsterbackend.entity.PostEntity;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
@DecoratedWith(PostMapperDecorator.class)
public interface PostMapper {
    @Mapping(target = "postId", source = "id")
    PostDto entityToDto(PostEntity postEntity);

    @Mapping(target = "id", source = "postId")
    PostEntity dtoToEntity(PostDto postDTO);

    @Mapping(target = "id", ignore = true)
    PostEntity updatePost(@MappingTarget PostEntity oldPost, PostEntity newPost);
}
