package com.roomster.roomsterbackend.service.impl;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.entity.DistrictEntity;
import com.roomster.roomsterbackend.mapper.DistrictMapper;
import com.roomster.roomsterbackend.repository.DistrictRepository;
import com.roomster.roomsterbackend.service.IService.IDistrictService;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistrictServiceImpl implements IDistrictService {
    @Autowired
    DistrictRepository districtRepository;

    @Autowired
    DistrictMapper districtMapper;

    @Override
    public ResponseEntity<?> findAll() {
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = new ResponseEntity<>(districtRepository.findAll().stream().map(districtMapper::entityToDTO), HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.OK);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> findByIdCity(Long id) {
        ResponseEntity<?> responseEntity;
        try {
            List<DistrictEntity> district = districtRepository.getAllDistrictByCityId(id);
            responseEntity = new ResponseEntity<>(district.stream().map(districtMapper::entityToDTO), HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.OK);
        }

        return responseEntity;
    }
}
