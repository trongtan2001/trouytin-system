package com.roomster.roomsterbackend.service.impl.payment;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.payment.BankMethodDto;
import com.roomster.roomsterbackend.entity.BankMethodEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import com.roomster.roomsterbackend.mapper.BankMethodMapper;
import com.roomster.roomsterbackend.repository.BankMethodRepository;
import com.roomster.roomsterbackend.service.IService.payment.IBankMethodService;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BankMethodService implements IBankMethodService {

    @Autowired
    private BankMethodRepository bankMethodRepository;

    @Autowired
    private BankMethodMapper bankMethodMapper;

    @Override
    public ResponseEntity<?> addBankMethod(BankMethodDto bankMethodDto, Principal connectedUser) {
        ResponseEntity<?> response = null;
        try {
            var user = (UserEntity)((UsernamePasswordAuthenticationToken)connectedUser).getPrincipal();
            if(user != null){
                BankMethodEntity bankMethodEntity = bankMethodMapper.dtoToEntity(bankMethodDto);
                bankMethodEntity.setUser(user);
                bankMethodRepository.save(bankMethodEntity);
                response = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_ADD_SUCCESS), HttpStatus.OK);
            }else {
                response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_USER_BY_TOKEN_NOT_FOUND), HttpStatus.NOT_FOUND);
            }
        }catch (Exception ex){
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Override
    public ResponseEntity<?> updateBankMethod(BankMethodDto bankMethodDto, Long bankMethodId) {
        ResponseEntity<?> response = null;
        try {
            Optional<BankMethodEntity> methodEntity = bankMethodRepository.findById(bankMethodId);
            if(methodEntity.isPresent()){
                methodEntity.get().setBankName(bankMethodDto.getBankName());
                methodEntity.get().setBankAccount(bankMethodDto.getBankAccount());
                bankMethodRepository.save(methodEntity.get());
                response = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_UPDATE_SUCCESS), HttpStatus.OK);
            }else {
                response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_BANK_METHOD_NOT_FOUND), HttpStatus.NOT_FOUND);
            }

        }catch (Exception ex){
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Override
    public ResponseEntity<?> deleteBankMethod(List<String> ids) {
        ResponseEntity<?> response = null;
        try {
            for (String id : ids) {
                Long bankId = Long.parseLong(id);
                Optional<BankMethodEntity> bankMethodEntity = bankMethodRepository.findById(bankId);
                if(bankMethodEntity.isPresent()){
                    bankMethodRepository.deleteById(bankId);
                }else {
                    return new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_BANK_METHOD_NOT_FOUND), HttpStatus.OK);
                }
            }
            response = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_DELETE_SUCCESS), HttpStatus.OK);
        }catch (Exception ex){
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Override
    public ResponseEntity<?> getAllBankMethodByUserId(Principal connectedUser) {
        ResponseEntity<?> response = null;
        try {
            var user = (UserEntity)((UsernamePasswordAuthenticationToken)connectedUser).getPrincipal();
            if(user != null) {
                List<BankMethodDto> list = bankMethodRepository.findAllByUserId(user.getId())
                        .stream()
                        .map(bankMethodEntity -> bankMethodMapper.entityToDTO(bankMethodEntity))
                        .collect(Collectors.toList());
                response = new ResponseEntity<>(list, HttpStatus.OK);
            }else {
                response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_USER_BY_TOKEN_NOT_FOUND), HttpStatus.NOT_FOUND);
            }
        }catch (Exception ex){
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
            return response;
    }

    @Override
    public ResponseEntity<?> getBankMethodById(Long bankId) {
        ResponseEntity<?> response = null;
        try {
            var bank = bankMethodRepository.findById(bankId);
            if(bank.isPresent()) {
                response = new ResponseEntity<>(bankMethodMapper.entityToDTO(bank.get()), HttpStatus.OK);
            }else {
                response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_USER_BY_TOKEN_NOT_FOUND), HttpStatus.NOT_FOUND);
            }
        }catch (Exception ex){
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
