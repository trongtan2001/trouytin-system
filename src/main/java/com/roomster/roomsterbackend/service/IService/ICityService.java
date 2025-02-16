package com.roomster.roomsterbackend.service.IService;

import org.springframework.http.ResponseEntity;

public interface ICityService {
    ResponseEntity<?> findAll();

    ResponseEntity<?> findById(Long id);
}
