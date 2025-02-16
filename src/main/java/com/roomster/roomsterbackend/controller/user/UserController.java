package com.roomster.roomsterbackend.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.auth.ChangePasswordRequest;
import com.roomster.roomsterbackend.dto.auth.ChangePhoneNumberRequest;
import com.roomster.roomsterbackend.dto.auth.OtpRequestDto;
import com.roomster.roomsterbackend.dto.auth.OtpValidationRequestDto;
import com.roomster.roomsterbackend.dto.user.ChangePhoneNumberWithOTP;
import com.roomster.roomsterbackend.dto.user.UpdateProfileRequest;
import com.roomster.roomsterbackend.dto.user.UserDto;
import com.roomster.roomsterbackend.service.IService.ITransactionService;
import com.roomster.roomsterbackend.service.IService.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;


@RestController
@RequestMapping("/api/v1/user")
@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_MANAGE','ROLE_ADMIN')")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    private final IUserService userService;

    private final ITransactionService transactionService;

    @GetMapping("/view-profile")
    public UserDto viewProfile(Principal connectedUser) {
        return userService.viewProfile(connectedUser);
    }

    @PostMapping(value = "/update-profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse saveNewPost(@RequestPart String profileRequest, @RequestPart(required = false, name = "images") @Valid MultipartFile images, Principal connectedUser) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        UpdateProfileRequest profile = objectMapper.readValue(profileRequest, UpdateProfileRequest.class);

        return userService.updateProfile(profile, images, connectedUser);
    }

    @PatchMapping("/update-password")
    public BaseResponse changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, Principal connectedUser) {
        return userService.changePassword(changePasswordRequest, connectedUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/get-user")
    public UserDto getUserById(Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/sendOTP")
    public ResponseEntity<?> sendOTP(@RequestBody OtpRequestDto requestDto) {
        return userService.sendOTP(requestDto);
    }

    @PostMapping("/up-to-role-manage")
    public ResponseEntity<?> upRoleUserToManage(@RequestBody OtpValidationRequestDto otpValidationRequestDto, Principal connectedUser) {
        return userService.upRoleToManage(otpValidationRequestDto, connectedUser);
    }

    @PostMapping(value = "/update-phonenumber")
    public ResponseEntity<?> changePhoneNumber(@RequestBody ChangePhoneNumberRequest request, Principal connectedUser) {
        return userService.changePhoneNumber(request, connectedUser);
    }

    @PostMapping(value = "/update-phonenumber-otp")
    public ResponseEntity<?> changePhoneNumberWithOTP(@RequestBody ChangePhoneNumberWithOTP request, Principal connectedUser) {
        return userService.changePhoneNumberWithOTP(request, connectedUser);
    }

    @PostMapping(value = "/service/purchasePackageByUser")
    public ResponseEntity<?> purchasePackageByUser(@RequestParam Long servicePackageId, Principal connectedUser) {
        return transactionService.purchasePackageByUser(connectedUser, servicePackageId);
    }

    //TODO: get service package by user
    @GetMapping(value = "/transaction/service-package")
    public ResponseEntity<?> getAllTransactionServiceByUser(Principal connectedUser,
                                                            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionService.getAllTransactionServiceByUser(connectedUser, pageable);
    }

    @GetMapping(value = "/service/valid-ulti-manager")
    public ResponseEntity<?> isValidUltiManager(Principal connectedUser) {
        return transactionService.isValidUltiManager(connectedUser);
    }
}
