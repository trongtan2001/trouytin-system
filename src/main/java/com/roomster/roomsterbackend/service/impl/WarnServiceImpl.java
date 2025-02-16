package com.roomster.roomsterbackend.service.impl;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.entity.WardEntity;
import com.roomster.roomsterbackend.mapper.WardMapper;
import com.roomster.roomsterbackend.repository.WarnRepository;
import com.roomster.roomsterbackend.service.IService.IWarnService;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarnServiceImpl implements IWarnService {
    @Autowired
    WarnRepository warnRepository;

    @Autowired
    WardMapper wardMapper;

    @Override
    public ResponseEntity<?> findAll() {
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = new ResponseEntity<>(warnRepository.findAll().stream().map(wardMapper::entityToDTO), HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.OK);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> findByIdCity(Long id) {
        ResponseEntity<?> responseEntity;
        try {
            List<WardEntity> wards = warnRepository.getAllWarnByDistrictId(id);
            responseEntity = new ResponseEntity<>(wards.stream().map(wardMapper::entityToDTO), HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.OK);
        }

        return responseEntity;
    }
}
