package com.roomster.roomsterbackend.service.impl;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.entity.RoleEntity;
import com.roomster.roomsterbackend.repository.RoleRepository;
import com.roomster.roomsterbackend.service.IService.IRoleService;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService implements IRoleService {
    @Autowired
    private RoleRepository repository;

    @Override
    public ResponseEntity<?> getAll() {
        ResponseEntity<?> response = null;
        try {
            List<RoleEntity> list = repository.findAll();
            response = new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Override
    public ResponseEntity<?> addRole(RoleEntity role) {
        ResponseEntity<?> response = null;
        try {
            RoleEntity roleEntity = repository.findByName(role.getName());
            if (roleEntity == null) {
                repository.save(role);
                response = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_ADD_SUCCESS), HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROLE_EXITED), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
