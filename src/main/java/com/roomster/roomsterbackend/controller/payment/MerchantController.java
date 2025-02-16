package com.roomster.roomsterbackend.controller.payment;

import com.roomster.roomsterbackend.dto.merchant.MerchantDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.roomster.roomsterbackend.service.IService.payment.IMerchantService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final IMerchantService merchantService;

    @PostMapping()
    public ResponseEntity<?> addMerchant(@RequestBody MerchantDto request){
        return merchantService.addMerchant(request);
    }

    @GetMapping
    public ResponseEntity<?> getAllMerchant(){
        return merchantService.getAllMerchant();
    }
}
