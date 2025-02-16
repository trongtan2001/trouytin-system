package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.service.transaction.TransactionDto;
import com.roomster.roomsterbackend.entity.TransactionEntity;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@DecoratedWith(TransactionMapperDecorator.class)
public interface TransactionMapper {
    @Mapping(target = "transactionId", source = "id")
    TransactionDto entityToDto(TransactionEntity transaction);
}
