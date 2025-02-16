package com.roomster.roomsterbackend.service.IService;

import com.roomster.roomsterbackend.entity.RoleEntity;
import org.springframework.http.ResponseEntity;

public interface IRoleService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> addRole(RoleEntity role);
}
