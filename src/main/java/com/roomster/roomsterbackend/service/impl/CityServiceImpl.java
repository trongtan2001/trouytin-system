package com.roomster.roomsterbackend.service.impl;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.entity.CityEntity;
import com.roomster.roomsterbackend.mapper.CityMapper;
import com.roomster.roomsterbackend.repository.CityRepository;
import com.roomster.roomsterbackend.service.IService.ICityService;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CityServiceImpl implements ICityService {
    @Autowired
    CityRepository cityRepository;

    @Autowired
    CityMapper cityMapper;

    @Override
    public ResponseEntity<?> findAll() {
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = new ResponseEntity<>(cityRepository.findAll().stream().map(cityMapper::entityToDTO), HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.OK);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        ResponseEntity<?> responseEntity;
        try {
            Optional<CityEntity> city = cityRepository.findById(id);
            if (city.isPresent()) {
                responseEntity = new ResponseEntity<>(cityMapper.entityToDTO(city.get()), HttpStatus.OK);
            } else {
                responseEntity = new ResponseEntity<>(MessageUtil.MSG_CITY_NOT_FOUND, HttpStatus.OK);
            }
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.OK);
        }

        return responseEntity;
    }
}
