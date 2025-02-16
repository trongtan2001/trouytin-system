package com.roomster.roomsterbackend.service.IService.ultiManager;

import com.roomster.roomsterbackend.dto.admin.HouseDto;
import org.springframework.http.ResponseEntity;

public interface IHouseService {

	ResponseEntity<?> getAllHouses(String price, String acreage, String stayMax, String status);

	ResponseEntity<?> getHouseById(String id);

	ResponseEntity<?> createHouse(HouseDto house);

	ResponseEntity<?> updateHouse(String id, HouseDto house);

	ResponseEntity<?> deleteHouse(String id);

    ResponseEntity<?> getStatusHouse();
}
