package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.admin.HouseDto;
import com.roomster.roomsterbackend.entity.HouseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HouseMapper {
    @Mapping(target = "houseId", source = "houseId")
    @Mapping(target = "houseName", source = "houseName")
    @Mapping(target = "warnId", source = "warnId")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "rooms", source = "rooms")
    HouseDto entityToDTO(HouseEntity house);

    @Mapping(target = "houseId", source = "houseId")
    @Mapping(target = "houseName", source = "houseName")
    @Mapping(target = "warnId", source = "warnId")
    @Mapping(target = "address", source = "address")
    HouseEntity dtoToEntity(HouseDto houseDto);
}
