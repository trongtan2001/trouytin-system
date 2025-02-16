package com.roomster.roomsterbackend.service.IService;

import com.roomster.roomsterbackend.dto.service.servicePackage.ServicePackageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface IServicePackageService {
    ResponseEntity<?> addServicePackage(ServicePackageDto request);
    ResponseEntity<?> updateServicePackage(Long servicePackageId, ServicePackageDto request);
    ResponseEntity<?> removeServicePackageById(Long servicePackageId);
    ResponseEntity<?> getAllServicePackage(Pageable pageable);

}
