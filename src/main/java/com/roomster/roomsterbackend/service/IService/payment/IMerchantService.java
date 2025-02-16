package com.roomster.roomsterbackend.service.IService.payment;

import com.roomster.roomsterbackend.dto.merchant.MerchantDto;
import org.springframework.http.ResponseEntity;

public interface IMerchantService {
    ResponseEntity<?> addMerchant(MerchantDto request);

    ResponseEntity<?> getAllMerchant();
}
