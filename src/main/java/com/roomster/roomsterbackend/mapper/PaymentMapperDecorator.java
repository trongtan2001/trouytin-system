package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.payment.PaymentDtoMapper;
import com.roomster.roomsterbackend.entity.PaymentEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import com.roomster.roomsterbackend.repository.UserRepository;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;

public abstract class PaymentMapperDecorator implements PaymentMapper {

    @Autowired
    @Qualifier("delegate")
    private PaymentMapper delegate;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PaymentDtoMapper entityToDto(PaymentEntity paymentEntity) {
        PaymentDtoMapper paymentDtoMapper = delegate.entityToDto(paymentEntity);
        paymentDtoMapper.setPaymentDestinationsName(paymentEntity.getPaymentDestinations().getId());
        Optional<UserEntity> userEntity = userRepository.findById(paymentEntity.getUserPayment().getId());
        userEntity.ifPresent(entity -> paymentDtoMapper.setPhoneNumberUser(entity.getPhoneNumber()));
        return paymentDtoMapper;
    }
}
