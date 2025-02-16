package com.roomster.roomsterbackend.service.impl.ultiManger;


import java.util.List;
import java.util.Optional;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.entity.InforRoomEntity;
import com.roomster.roomsterbackend.repository.RoomRepository;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import com.roomster.roomsterbackend.util.validator.ValidatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.roomster.roomsterbackend.entity.TenantEntity;
import com.roomster.roomsterbackend.repository.TenantRepository;
import com.roomster.roomsterbackend.service.IService.ultiManager.ITenantService;

@Service
public class TenantServiceImpl implements ITenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private RoomRepository inforRoomRepository;

    @Override
    public ResponseEntity<?> getAllTenant() {
        try {
            List<TenantEntity> tenants = tenantRepository.findAll();
            return ResponseEntity.ok(tenants);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving tenants", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getGuestById(String id) {
        ResponseEntity<?> responseEntity;
        try {
            TenantEntity tenant = tenantRepository.findById(Long.parseLong(id)).orElse(null);
            if (tenant != null) {
                responseEntity = ResponseEntity.ok(tenant);
            } else {
                responseEntity = new ResponseEntity<>("Tenant not found", HttpStatus.NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            responseEntity=  new ResponseEntity<>("Invalid tenant ID format", HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> getTenantByRoomId(String id) {
        ResponseEntity<?> responseEntity;
        try {
            List<TenantEntity> tenants = tenantRepository.findByRoomId(Long.parseLong(id));
            responseEntity =  ResponseEntity.ok(tenants);
        } catch (NumberFormatException e) {
            responseEntity =  new ResponseEntity<>("Invalid house ID format", HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    private boolean isValidTenant(TenantEntity tenant) {
        // Validate email uniqueness
        if (isDuplicateEmail(tenant.getEmail(), tenant.getId())) {
            // Handle duplicate email validation error
            return false;
        }

        // Validate phone number uniqueness
        if (isDuplicatePhoneNumber(tenant.getPhoneNumber(), tenant.getId())) {
            // Handle duplicate phone number validation error
            return false;
        }

        // Validate identifier uniqueness
        if (isDuplicateIdentifier(tenant.getIdentifier(), tenant.getId())) {
            // Handle duplicate identifier validation error
            return false;
        }

        // If all validations pass
        return true;
    }

    private boolean isDuplicateEmail(String email, Long tenantId) {
        // Query to check if there's another tenant with the same email
        Long count = tenantRepository.countSameEmail(email, tenantId);
        return count > 0;
    }

    private boolean isDuplicatePhoneNumber(String phoneNumber, Long tenantId) {
        // Query to check if there's another tenant with the same phone number
        Long count = tenantRepository.countSamePhoneNumber(phoneNumber, tenantId);
        return count > 0;
    }

    private boolean isDuplicateIdentifier(String identifier, Long tenantId) {
        // Query to check if there's another tenant with the same identifier
        Long count = tenantRepository.countSameIdentifier(identifier, tenantId);
        return count > 0;
    }

    @Override
    public ResponseEntity<?> createTenant(TenantEntity tenant) {
        ResponseEntity<?> responseEntity;
        try {
            if (isValidTenant(tenant)) {
                Optional<InforRoomEntity> inforRoomEntityOptional = inforRoomRepository.findById(tenant.getIdRoom());
                if (inforRoomEntityOptional.isPresent()) {
                    InforRoomEntity room = inforRoomEntityOptional.get();
                    if (room.getTenantList().size() < room.getStayMax()) {
                        tenantRepository.save(tenant);
                        room.setEmptyRoom(1);
                        inforRoomRepository.save(room);
                        responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_ADD_SUCCESS), HttpStatus.OK);
                    } else {
                        responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_IS_FULL), HttpStatus.BAD_REQUEST);
                    }
                } else {
                    responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_NOT_FOUND), HttpStatus.BAD_REQUEST);
                }
            } else {
                responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_TENANT_EXISTS), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> updateTenant(String id, TenantEntity newestTenant) {
        ResponseEntity<?> responseEntity;
        try {
            if (ValidatorUtils.isNumber(id)) {
                Long idL = Long.parseLong(id);
                Optional<TenantEntity> tenantOptional = tenantRepository.findById(idL);
                if (tenantOptional.isPresent()) {
                    if (isValidTenant(newestTenant)) {
                        TenantEntity oldTenant = tenantOptional.get();
                        Optional<InforRoomEntity> inforRoomEntityOptional = inforRoomRepository.findById(newestTenant.getIdRoom());
                        if (inforRoomEntityOptional.isPresent()) {
                            InforRoomEntity room = inforRoomEntityOptional.get();
                            if (room.getTenantList().size() <= room.getStayMax()) {
                                oldTenant.setName(newestTenant.getName());
                                oldTenant.setAge(newestTenant.getAge());
                                oldTenant.setGender(newestTenant.getGender());
                                oldTenant.setPhoneNumber(newestTenant.getPhoneNumber());
                                oldTenant.setIdentifier(newestTenant.getIdentifier());
                                oldTenant.setEmail(newestTenant.getEmail());
                                // Assuming you want to update the room association as well
                                oldTenant.setIdRoom(newestTenant.getIdRoom());
                                // Save the updated tenant
                                tenantRepository.save(oldTenant);
                                responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_UPDATE_SUCCESS), HttpStatus.OK);
                            } else {
                                responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_IS_FULL), HttpStatus.BAD_REQUEST);
                            }
                        } else {
                            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_NOT_FOUND), HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_TENANT_EXISTS), HttpStatus.BAD_REQUEST);
                    }
                } else {
                    responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_TENANT_NOT_FOUND), HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> deleteTenant(List<String> tenantIds) {
        ResponseEntity<?> responseEntity;
        try {
            for (String id : tenantIds) {
                tenantRepository.deleteById(Long.parseLong(id));
            }
            responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_DELETE_SUCCESS), HttpStatus.OK);
        } catch (NumberFormatException e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> moveTenant(String id, List<String> tenantIds) {
        ResponseEntity<?> responseEntity;
        try {
            // Find the target room
            if (ValidatorUtils.isNumber(id)) {
                Long idL = Long.parseLong(id);
                Optional<InforRoomEntity> targetRoom = inforRoomRepository.findById(idL);
                if(tenantIds == null || tenantIds.size() == 0){
                    return new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_TENANT_NOT_CHECK), HttpStatus.BAD_REQUEST);
                }
                if (!targetRoom.isPresent()) {
                    return new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_NOT_FOUND), HttpStatus.BAD_REQUEST);
                }
                if (targetRoom.get().getStayMax() - targetRoom.get().getTenantList().size() < tenantIds.size()) {
                    // If the room is not empty, return an error
                    return new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_NOT_VALID), HttpStatus.BAD_REQUEST);
                }
                InforRoomEntity room = targetRoom.get();
                Long idCurrentRoom = 0L;
                // Move tenants to the target room
                for (String tenantId : tenantIds) {
                    if (ValidatorUtils.isNumber(tenantId)) {
                        Long idt = Long.parseLong(tenantId);
                        Optional<TenantEntity> tenantOptional = tenantRepository.findById(idt);
                        if (tenantOptional.isPresent()) {
                            TenantEntity oldTenant = tenantOptional.get();
                            idCurrentRoom = oldTenant.getIdRoom();
                            oldTenant.setIdRoom(room.getId());
                            oldTenant.setRoom(room);
                            tenantRepository.save(oldTenant);
                        } else {
                            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_TENANT_NOT_FOUND), HttpStatus.BAD_REQUEST);
                            break;
                        }
                    } else {
                        responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ID_FORMAT_INVALID), HttpStatus.BAD_REQUEST);
                        break;
                    }
                }
                if (idCurrentRoom != 0){
                    Optional<InforRoomEntity> roomEntityOptional = inforRoomRepository.findById(idCurrentRoom);
                    if (roomEntityOptional.isPresent()){
                        InforRoomEntity curentRoom = roomEntityOptional.get();
                        if(curentRoom.getTenantList().size()==0) {
                            curentRoom.setEmptyRoom(0);
                            inforRoomRepository.save(curentRoom);
                        }
                    }
                }
                responseEntity = new ResponseEntity<>(BaseResponse.success("Di chuyển thành công"), HttpStatus.OK);
            } else {
                responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ID_FORMAT_INVALID), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            Long idL = Long.parseLong(id);
            Optional<InforRoomEntity> targetRoom = inforRoomRepository.findById(idL);
            InforRoomEntity room = targetRoom.get();
            room.setEmptyRoom(1);
            inforRoomRepository.save(room);
        }
        return responseEntity;
    }
}
