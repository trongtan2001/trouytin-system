package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.payment.PaymentDtoMapper;
import com.roomster.roomsterbackend.entity.PaymentEntity;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@DecoratedWith(PaymentMapperDecorator.class)
public interface PaymentMapper {
    PaymentDtoMapper entityToDto(PaymentEntity paymentEntity);
}
