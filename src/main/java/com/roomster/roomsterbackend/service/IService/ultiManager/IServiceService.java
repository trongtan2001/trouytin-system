package com.roomster.roomsterbackend.service.IService.ultiManager;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.roomster.roomsterbackend.entity.ServiceHouseEntity;

public interface IServiceService {

	ResponseEntity<?> findAll();

	ResponseEntity<?>  getServiceHouseById(String id);

	ResponseEntity<?>  createServiceHouse(ServiceHouseEntity serviceHouse);

	ResponseEntity<?>  updateServiceHouse(String id, ServiceHouseEntity serviceHouse);

	ResponseEntity<?>  deleteServiceHouse(List<String> listServices);
	
	ResponseEntity<?>  updateServiceHouseByRoomId(String id, List <String> listServiceIds);
	
	
	

}
