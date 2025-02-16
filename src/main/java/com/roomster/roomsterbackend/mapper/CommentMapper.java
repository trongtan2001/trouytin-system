package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.comment.CommentPostDto;
import com.roomster.roomsterbackend.entity.CommentEnity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "commentPostId", source = "id")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "modifiedDate", source = "modifiedDate")
    CommentPostDto entityToDTO(CommentEnity commentEnity);

    @Mapping(target = "id", source = "commentPostId")
    CommentEnity dtoToEntity(CommentPostDto commentPostDTO);

}
