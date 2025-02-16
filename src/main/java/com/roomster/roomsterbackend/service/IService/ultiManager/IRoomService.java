package com.roomster.roomsterbackend.service.IService.ultiManager;

import com.roomster.roomsterbackend.entity.InforRoomEntity;
import org.springframework.http.ResponseEntity;

public interface IRoomService {
    ResponseEntity<?> findAll();
    ResponseEntity<?> save(InforRoomEntity room);

    ResponseEntity<?> update(String id, InforRoomEntity newestRoom);

    ResponseEntity<?> findById(String id);

    ResponseEntity<?> delete(String id);
    
    ResponseEntity<?>  findServicesByRoomId(String id);

    ResponseEntity<?> getStatusRoom();

    ResponseEntity<?> getStatusPayment();
}
