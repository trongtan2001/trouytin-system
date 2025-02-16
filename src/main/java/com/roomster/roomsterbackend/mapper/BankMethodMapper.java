package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.payment.BankMethodDto;
import com.roomster.roomsterbackend.entity.BankMethodEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankMethodMapper {
    @Mapping(target = "bankMethodId", source = "id")
    BankMethodDto entityToDTO(BankMethodEntity bankMethodEntity);

    @Mapping(target = "id", source = "bankMethodId")
    BankMethodEntity dtoToEntity(BankMethodDto bankMethodDto);
}
