package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.service.servicePackage.PartServicePackage;
import com.roomster.roomsterbackend.dto.service.transaction.TransactionDto;
import com.roomster.roomsterbackend.dto.user.PartUser;
import com.roomster.roomsterbackend.entity.ServicePackageEntity;
import com.roomster.roomsterbackend.entity.TransactionEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import com.roomster.roomsterbackend.repository.ServicePackageRepository;
import com.roomster.roomsterbackend.repository.UserRepository;
import com.roomster.roomsterbackend.util.validator.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;

public abstract class TransactionMapperDecorator implements TransactionMapper {
    @Autowired
    @Qualifier("delegate")
    private TransactionMapper delegate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServicePackageRepository servicePackageRepository;

    @Override
    public TransactionDto entityToDto(TransactionEntity transaction) {
        TransactionDto transactionDto = delegate.entityToDto(transaction);
        Optional<UserEntity> user = userRepository.findById(transaction.getUserTransaction().getId());
        Optional<ServicePackageEntity> servicePackage = servicePackageRepository.findById(transaction.getServicePackage().getId());
        if (user.isPresent()) {
            PartUser partUser = PartUser.builder()
                    .userId(user.get().getId())
                    .userName(user.get().getUserName())
                    .phoneNumber(PhoneNumberValidator.normalizeDisplayPhoneNumber(user.get().getPhoneNumber()))
                    .images(user.get().getImages())
                    .build();
            transactionDto.setPartUser(partUser);
        }
        if(servicePackage.isPresent()){
            PartServicePackage partServicePackage = PartServicePackage.builder()
                    .servicePackageId(servicePackage.get().getId())
                    .servicePackageName(servicePackage.get().getName())
                    .durationDays(servicePackage.get().getDurationDays())
                    .price(servicePackage.get().getPrice())
                    .createdDate(servicePackage.get().getCreatedDate())
                    .build();
            transactionDto.setPartServicePackage(partServicePackage);
        }
        return transactionDto;
    }
}
