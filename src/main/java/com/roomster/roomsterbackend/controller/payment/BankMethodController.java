package com.roomster.roomsterbackend.controller.payment;

import com.roomster.roomsterbackend.dto.payment.BankMethodDto;
import com.roomster.roomsterbackend.service.IService.payment.IBankMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/bank")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_MANAGE','ROLE_ADMIN', 'ROLE_ULTI_MANAGER')")
public class BankMethodController {

    private final IBankMethodService bankMethodService;

    @PostMapping()
    public ResponseEntity<?> bankMethod(@RequestBody BankMethodDto bankMethodDto, Principal connectedUser){
      return bankMethodService.addBankMethod(bankMethodDto, connectedUser);
    }


    @GetMapping()
    public ResponseEntity<?> getAllByUserId(Principal connectedUser){
        return bankMethodService.getAllBankMethodByUserId(connectedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(name = "id") Long bankId){
        return bankMethodService.getBankMethodById(bankId);
    }

    @PutMapping("/{bankMethodId}")
    public ResponseEntity<?> updateBankMethod(@RequestBody BankMethodDto bankMethodDto, @PathVariable Long bankMethodId){
        return bankMethodService.updateBankMethod(bankMethodDto, bankMethodId);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteBankMethod(@RequestBody List<String> listBankMethodIds){
        return bankMethodService.deleteBankMethod(listBankMethodIds);
    }
}
