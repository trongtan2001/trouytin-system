package com.roomster.roomsterbackend.service.IService.ultiManager;

import org.springframework.http.ResponseEntity;

import com.roomster.roomsterbackend.entity.TenantEntity;

import java.util.List;

public interface ITenantService {

	
	ResponseEntity<?> getAllTenant();

	ResponseEntity<?> getGuestById(String id);
	
	ResponseEntity<?> getTenantByRoomId(String id);

	ResponseEntity<?> createTenant(TenantEntity tenant);

	ResponseEntity<?> updateTenant(String id, TenantEntity tenant);

	ResponseEntity<?> deleteTenant(List<String> tenantIds);

	ResponseEntity<?> moveTenant(String id, List<String> tenantIds);
}
