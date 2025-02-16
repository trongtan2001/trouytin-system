package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.postType.PostTypeDto;
import com.roomster.roomsterbackend.entity.PostTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostTypeMapper {
    PostTypeDto entityToDto(PostTypeEntity postTypeEntity);
    PostTypeEntity dtoToEntity(PostTypeDto postTypeDto);
}
