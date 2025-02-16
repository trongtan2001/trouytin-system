package com.roomster.roomsterbackend.service.IService.payment;

import com.roomster.roomsterbackend.dto.payment.BankMethodDto;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface IBankMethodService {
    ResponseEntity<?> addBankMethod(BankMethodDto bankMethodDto, Principal connectedUser);
    ResponseEntity<?> updateBankMethod(BankMethodDto bankMethodDto ,Long bankMethodId);
    ResponseEntity<?> deleteBankMethod(List<String> ids);
    ResponseEntity<?> getAllBankMethodByUserId(Principal connectedUser);

    ResponseEntity<?> getBankMethodById(Long bankId);
}
