package com.roomster.roomsterbackend.service.impl.ultiManger;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.inforRoom.InforRoomPaymentStatusDto;
import com.roomster.roomsterbackend.dto.order.OrderStatusPaymentDto;
import com.roomster.roomsterbackend.entity.*;
import com.roomster.roomsterbackend.repository.HouseRepository;
import com.roomster.roomsterbackend.repository.RoomRepository;
import com.roomster.roomsterbackend.service.IService.ultiManager.IRoomService;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import com.roomster.roomsterbackend.util.validator.ValidatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements IRoomService {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    HouseRepository houseRepository;

    @Override
    public ResponseEntity<?> findAll() {
        ResponseEntity<?> responseEntity;
        try {
            handleChangerRoomStatus();
            List<InforRoomEntity> inforRoomEntityList = roomRepository.findAll();
            responseEntity = new ResponseEntity<>(inforRoomEntityList, HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    private boolean isValidRoom(InforRoomEntity room) {
        Long countRoomStt = this.roomRepository.countRoomsDifferentStt(room.getNumberRoom(), room.getId(), room.getHouseId());
        return countRoomStt == 0;
    }

    @Override
    public ResponseEntity<?> save(InforRoomEntity room) {
        ResponseEntity<?> responseEntity;
        try {
            // check house
            Long houseId = room.getHouseId();
            Optional<HouseEntity> house = this.houseRepository.findById(houseId);
            if (house.isPresent()) {
                // validate room
                if(isValidRoom(room)){
                    room = roomRepository.save(room);
                    responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_ADD_SUCCESS), HttpStatus.OK);
                } else{
                    responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_EXISTS), HttpStatus.BAD_REQUEST);
                }
            } else {
                responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_HOUSE_NOT_FOUND), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> update(String id, InforRoomEntity newestRoom) {
        ResponseEntity<?> responseEntity;
        try {
            if (ValidatorUtils.isNumber(id)) {
                Long idL = Long.parseLong(id);
                Optional<InforRoomEntity> room = this.roomRepository.findById(idL);
                if (room.isPresent()) {
                    InforRoomEntity oldRoom = room.get();
                    if(isValidRoom((newestRoom))){
                        oldRoom.setEmptyRoom(newestRoom.getEmptyRoom());
                        oldRoom.setNumberRoom(newestRoom.getNumberRoom());
                        oldRoom.setAcreage(newestRoom.getAcreage());
                        oldRoom.setPost(newestRoom.getPost());
                        oldRoom.setHouseId(newestRoom.getHouseId());
                        oldRoom.setPrice(newestRoom.getPrice());
                        oldRoom.setStayMax(newestRoom.getStayMax());
                        oldRoom.setWaterPrice(newestRoom.getWaterPrice());
                        oldRoom.setElectricityPrice(newestRoom.getElectricityPrice());
                        oldRoom = this.roomRepository.save(oldRoom);
                        responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_UPDATE_SUCCESS), HttpStatus.OK);
                    }
                    else{
                        responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_EXISTS), HttpStatus.BAD_REQUEST);
                    }
                } else {
                    responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_NOT_FOUND), HttpStatus.BAD_REQUEST);
                }
            } else {
                responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ID_FORMAT_INVALID), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> findById(String id) {
        ResponseEntity<?> responseEntity;
        try {
            if (ValidatorUtils.isNumber(id)) {
                Long idL = Long.parseLong(id);
                Optional<InforRoomEntity> roomEntityOptional = this.roomRepository.findById(idL);
                if (roomEntityOptional.isPresent()) {
                    InforRoomEntity room = roomEntityOptional.get();
                    room.getOrders().sort(Comparator.comparing(OrderEntity::getPaymentDate, Comparator.reverseOrder()));
                    responseEntity = new ResponseEntity<>(room, HttpStatus.OK);
                } else {
                    responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_NOT_FOUND), HttpStatus.BAD_REQUEST);
                }
            } else {
                responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ID_FORMAT_INVALID), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        ResponseEntity<?> responseEntity;
        try {
            if (ValidatorUtils.isNumber(id)) {
                Long idL = Long.parseLong(id);
                Optional<InforRoomEntity> room = this.roomRepository.findById(idL);
                if (room.isPresent()) {
                    this.roomRepository.deleteById(idL);
                    responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_DELETE_SUCCESS), HttpStatus.OK);
                } else {
                    responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_NOT_FOUND), HttpStatus.BAD_REQUEST);
                }
            } else {
                responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ID_FORMAT_INVALID), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> findServicesByRoomId(String id) {
        ResponseEntity<?> responseEntity;
        try {
            if (ValidatorUtils.isNumber(id)) {
                Long roomId = Long.parseLong(id);
                Optional<InforRoomEntity> roomOptional = this.roomRepository.findById(roomId);

                if (roomOptional.isPresent()) {
                    InforRoomEntity room = roomOptional.get();
                    List<RoomServiceEntity> services = room.getServices();

                    responseEntity = new ResponseEntity<>(services, HttpStatus.OK);
                } else {
                    responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_NOT_FOUND), HttpStatus.BAD_REQUEST);
                }
            } else {
                responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ID_FORMAT_INVALID), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> getStatusRoom() {
        ResponseEntity<?> responseEntity;
        try {
            Long userId = 0L;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserEntity) {
                userId = ((UserEntity) principal).getId();
            }
            Long roomCount = this.roomRepository.count();
            Long emptyRoomCount = this.roomRepository.countEmptyRooms(userId);
            Long percent = Math.round((emptyRoomCount.doubleValue() / roomCount.doubleValue()) * 100.0);
            responseEntity = new ResponseEntity<>(percent, HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> getStatusPayment() {
        ResponseEntity<?> responseEntity;
        try {
            Long userId = 0L;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserEntity) {
                userId = ((UserEntity) principal).getId();
            }
            List<InforRoomEntity> roomsWithUnpaidPayments = roomRepository.findRoomsByPaymentStatusNotPaid(userId);
            List<InforRoomPaymentStatusDto> inforRoomPaymentStatusDtos = roomsWithUnpaidPayments.stream()
                    .map(room -> {
                        InforRoomPaymentStatusDto dto = new InforRoomPaymentStatusDto();
                        dto.setHouseName(room.getHouse().getHouseName());
                        dto.setRoomName(room.getNumberRoom() + "");

                        List<OrderStatusPaymentDto> orderStatusPayments = room.getOrders().stream()
                                .filter(order -> ("N".equals(order.getStatusPayment()) || "P".equals(order.getStatusPayment())))
                                .map(order -> new OrderStatusPaymentDto(order.getPaymentDate().toString()
                                        , order.getTotal().toString()
                                        ,order.getTotalPayment().toString()
                                        ,order.getTotal().subtract(order.getTotalPayment()).toString()
                                ))
                                .collect(Collectors.toList());

                        dto.setOrderStatusPayments(orderStatusPayments);
                        return dto;
                    })
                    .collect(Collectors.toList());
            responseEntity = new ResponseEntity<>(inforRoomPaymentStatusDtos, HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    private void handleChangerRoomStatus() {
        List<InforRoomEntity> inforRoomEntityList = roomRepository.findAll();
        for (InforRoomEntity room : inforRoomEntityList) {
            int tenantNum = room.getTenantList().size();
            if(tenantNum > 0){
                room.setEmptyRoom(1);
            }
            else{
                room.setEmptyRoom(0);
            }
            roomRepository.save(room);
        }
    }
}
