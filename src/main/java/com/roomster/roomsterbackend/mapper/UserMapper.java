package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.user.UserDto;
import com.roomster.roomsterbackend.entity.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {
    @Mappings({
            @Mapping(target = "roleList", ignore = true),
            @Mapping(target = "userId", source = "id"),
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "images", source = "images"),
            @Mapping(target = "userName", source = "userName")
    })
    UserDto entityToDto(UserEntity userEntity);

    @Mappings({
            @Mapping(target = "roles", ignore = true),
            @Mapping(target = "id", ignore = true)
    })
    UserEntity dtoToEntity(UserDto userDTO);

}
