package com.roomster.roomsterbackend.service.IService;

import org.springframework.http.ResponseEntity;

public interface IDistrictService {
    ResponseEntity<?> findAll();

    ResponseEntity<?> findByIdCity(Long id);
}
