package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.user.UserDto;
import com.roomster.roomsterbackend.entity.RoleEntity;
import com.roomster.roomsterbackend.entity.TransactionEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import com.roomster.roomsterbackend.repository.RoleRepository;
import com.roomster.roomsterbackend.service.IService.ITransactionService;
import com.roomster.roomsterbackend.util.validator.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class UserMapperDecorator implements UserMapper {
    @Autowired
    @Qualifier("delegate")
    private UserMapper delegate;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ITransactionService transactionService;
    @Override
    public UserEntity dtoToEntity(UserDto userDTO) {
        UserEntity userEntity = delegate.dtoToEntity(userDTO);
        Set<RoleEntity> userRoleEntityList = new HashSet<>();
        for (RoleEntity roleEntity : userDTO.getRoleList()) {
            userRoleEntityList.add(roleEntity);
        }
        if (userDTO.getPassword() != null) {
            userEntity.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }
        userEntity.setRoles(userRoleEntityList);
        return userEntity;
    }

    @Override
    public UserDto entityToDto(UserEntity userEntity) {
        UserDto userDTO = delegate.entityToDto(userEntity);
        userDTO.setPhoneNumber(userEntity.getPhoneNumber());
        Set<RoleEntity> roleDTOList = userEntity.getRoles();
        userDTO.setRoleList(roleDTOList);
        ResponseEntity<?> response = transactionService.purchasedServiceByUser();
        if(response.getStatusCode().is2xxSuccessful()){
            TransactionEntity transaction = (TransactionEntity)response.getBody();
            if(transaction != null){
                userDTO.setServicePackageUsed(transaction.getServicePackage().getName());
            }
        }
        return userDTO;
    }
}
