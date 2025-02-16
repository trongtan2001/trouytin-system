package com.roomster.roomsterbackend.service.impl;

import com.roomster.roomsterbackend.base.BaseResultWithDataAndCount;
import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.service.servicePackage.ServicePackageDto;
import com.roomster.roomsterbackend.entity.ServicePackageEntity;
import com.roomster.roomsterbackend.mapper.ServicePackageMapper;
import com.roomster.roomsterbackend.repository.ServicePackageRepository;
import com.roomster.roomsterbackend.service.IService.IServicePackageService;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicePackageServiceImpl implements IServicePackageService {
    @Autowired
    private ServicePackageRepository servicePackageRepository;

    @Autowired
    private ServicePackageMapper servicePackageMapper;

    @Override
    public ResponseEntity<?> addServicePackage(ServicePackageDto request) {
        ResponseEntity<?> responseEntity = null;
        try {
            Optional<ServicePackageEntity> servicePackage = servicePackageRepository.findByName(request.getName());
            if(servicePackage.isPresent()){
                return new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SERVICE_EXISTS), HttpStatus.BAD_REQUEST);
            }
            servicePackageRepository.save(servicePackageMapper.dtoToEntity(request));
            responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_ADD_SUCCESS), HttpStatus.OK);
        } catch (Exception ex) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> updateServicePackage(Long servicePackageId, ServicePackageDto request) {
        ResponseEntity<?> responseEntity = null;
        try {
            request.setServicePackageId(servicePackageId);
            ServicePackageEntity servicePackage = servicePackageMapper.dtoToEntity(request);
            servicePackageRepository.save(servicePackage);
            responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_UPDATE_SUCCESS), HttpStatus.OK);

        } catch (Exception ex) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> removeServicePackageById(Long servicePackageId) {
        ResponseEntity<?> responseEntity = null;
        try {
            servicePackageRepository.deleteById(servicePackageId);
            responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_DELETE_SUCCESS), HttpStatus.OK);
        } catch (Exception ex) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> getAllServicePackage(Pageable pageable) {
        ResponseEntity<?> responseEntity = null;
        BaseResultWithDataAndCount<List<ServicePackageDto>> resultWithCount = new BaseResultWithDataAndCount<>();
        try {
            List<ServicePackageDto> packageDtoList = servicePackageRepository.findAll(pageable)
                    .stream()
                    .map(servicePackage -> servicePackageMapper.entityToDto(servicePackage))
                    .collect(Collectors.toList());
            Long count = servicePackageRepository.count();
            resultWithCount.setCount(count);
            resultWithCount.setData(packageDtoList);
            responseEntity = new ResponseEntity<>(resultWithCount, HttpStatus.OK);
        } catch (Exception ex) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
}
