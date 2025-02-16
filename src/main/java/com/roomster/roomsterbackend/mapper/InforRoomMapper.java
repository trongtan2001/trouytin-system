package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.inforRoom.InforRoomDto;
import com.roomster.roomsterbackend.entity.InforRoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InforRoomMapper {

    @Mapping(target = "inforRoomId", source = "id")
    InforRoomDto entityToDto(InforRoomEntity inforRoomEntity);

    @Mapping(target = "id", source = "inforRoomId")
    InforRoomEntity dtoToEntity(InforRoomDto postDTO);

}
