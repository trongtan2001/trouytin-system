package com.roomster.roomsterbackend.service.IService;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.auth.ChangePasswordRequest;
import com.roomster.roomsterbackend.dto.auth.ChangePhoneNumberRequest;
import com.roomster.roomsterbackend.dto.auth.OtpRequestDto;
import com.roomster.roomsterbackend.dto.auth.OtpValidationRequestDto;
import com.roomster.roomsterbackend.dto.user.ChangePhoneNumberWithOTP;
import com.roomster.roomsterbackend.dto.user.UpdateProfileRequest;
import com.roomster.roomsterbackend.dto.user.UserDto;
import com.roomster.roomsterbackend.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

public interface IUserService {
    Optional<UserEntity> findByEmail(String email);

    ResponseEntity<?> findAllByIsDeletedIsFalse(Pageable pageable);
    ResponseEntity<?> findAllByIsDeletedIsTrue(Pageable pageable);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    UserDto viewProfile(Principal connectedUser);

    BaseResponse updateProfile(UpdateProfileRequest profileRequest, MultipartFile images, Principal connectedUser) throws IOException;

    BaseResponse changePassword(ChangePasswordRequest changePasswordRequest, Principal connectedUser);

    UserDto getUserById(Long userId);

    ResponseEntity<?> upRoleToManage(OtpValidationRequestDto otpValidationRequestDto, Principal connectedUser);

    ResponseEntity<?> sendOTP(OtpRequestDto requestDto);

    ResponseEntity<?> changePhoneNumber(ChangePhoneNumberRequest request, Principal connectedUser);

    ResponseEntity<?> changePhoneNumberWithOTP(ChangePhoneNumberWithOTP request, Principal connectedUser);

    ResponseEntity<?> findAllAccountByRoleName(Pageable pageable, String roleName);

    ResponseEntity<?> deleteUserByIds(Long[] listId);

    ResponseEntity<?> upRoleUserByAdmin(String roleName, Long userId);

    ResponseEntity<?> getUserAccountStatus();

}