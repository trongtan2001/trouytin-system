package com.roomster.roomsterbackend.service.IService;

import org.springframework.http.ResponseEntity;

public interface IWarnService {
    ResponseEntity<?> findAll();


    ResponseEntity<?> findByIdCity(Long districtId);
}
