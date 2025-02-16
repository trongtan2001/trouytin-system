package com.roomster.roomsterbackend.service.impl;


import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.base.ResponseDto;
import com.roomster.roomsterbackend.common.ModelCommon;
import com.roomster.roomsterbackend.common.Status;
import com.roomster.roomsterbackend.dto.auth.*;
import com.roomster.roomsterbackend.entity.RoleEntity;
import com.roomster.roomsterbackend.entity.TokenEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import com.roomster.roomsterbackend.repository.RoleRepository;
import com.roomster.roomsterbackend.repository.TokenRepository;
import com.roomster.roomsterbackend.repository.UserRepository;
import com.roomster.roomsterbackend.service.IService.IAuthenticationService;
import com.roomster.roomsterbackend.service.impl.twilio.TwilioOTPService;
import com.roomster.roomsterbackend.util.helpers.HashHelper;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import com.roomster.roomsterbackend.util.validator.PhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TwilioOTPService twilioOTPService;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final Map<String, RegisterRequest> registerAccounts = new HashMap<>();

    public BaseResponse register(RegisterRequest request) {
        if (!PhoneNumberValidator.isValidPhoneNumber(request.getPhoneNumber())) {
            return BaseResponse.error(MessageUtil.MSG_PHONE_NUMBER_FORMAT_INVALID);
        }

        Optional<UserEntity> existingUser = userRepository.findByPhoneNumber(PhoneNumberValidator.normalizePhoneNumber(request.getPhoneNumber()));

        if (existingUser.isPresent()) {
            return BaseResponse.error(MessageUtil.MSG_PHONE_NUMBER_IS_EXITED);
        }
        // Xóa tài khoản hiện tại nếu số điện thoại đã tồn tại
        if (registerAccounts.containsKey(request.getPhoneNumber())) {
            registerAccounts.remove(request.getPhoneNumber());
        }

        if (request.getRole().equals(ModelCommon.USER)) {
            boolean checkRegister = this.baseRegister(request);
            if (checkRegister) {
                return BaseResponse.success(MessageUtil.MSG_REGISTER_SUCCESS);
            }
        } else if (request.getRole().equals(ModelCommon.MANAGEMENT) || request.getRole().equals(ModelCommon.ADMIN)) {
            OtpRequestDto otpRequestDto = createOtpRequest(request);
            ResponseDto otpResponseDto = twilioOTPService.sendSMS(otpRequestDto);
            if (otpResponseDto.getStatus().equals(Status.DELIVERED)) {
                registerAccounts.put(request.getPhoneNumber(), request);
                return BaseResponse.success(MessageUtil.MSG_OTP_DELIVERED);
            } else {
                return BaseResponse.error(MessageUtil.MSG_OTP_FAILED);
            }
        }
        return BaseResponse.error(MessageUtil.MSG_REGISTER_FAIL);
    }

    @Override
    public ResponseEntity<?> registerByAdmin(RegisterRequest request) {
        ResponseEntity<?> response = null;
        try {
            if (!PhoneNumberValidator.isValidPhoneNumber(request.getPhoneNumber())) {
                response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_PHONE_NUMBER_FORMAT_INVALID), HttpStatus.BAD_REQUEST);
            } else {
                Optional<UserEntity> existingUser = userRepository.findByPhoneNumber(PhoneNumberValidator.normalizePhoneNumber(request.getPhoneNumber()));
                if (existingUser.isPresent()) {
                    response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_PHONE_NUMBER_IS_EXITED), HttpStatus.BAD_REQUEST);
                } else {
                    boolean checkRegister = this.baseRegister(request);
                    if (checkRegister) {
                        response = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_REGISTER_SUCCESS), HttpStatus.OK);
                    } else {
                        response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_REGISTER_FAIL), HttpStatus.BAD_REQUEST);
                    }
                }
            }
        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    private OtpRequestDto createOtpRequest(RegisterRequest request) {
        OtpRequestDto otpRequestDto = new OtpRequestDto();
        otpRequestDto.setPhoneNumber(request.getPhoneNumber());
        otpRequestDto.setUserName(request.getUserName());
        return otpRequestDto;
    }


    private boolean baseRegister(RegisterRequest request) {
        RoleEntity role = roleRepository.findByName(request.getRole());
        if (role != null) {
            UserEntity user = new UserEntity();
            user.setId((long) HashHelper.generateRandomNumbers());
            user.setUserName(request.getUserName());
            user.setBalance(new BigDecimal(0));
            user.setPhoneNumber(PhoneNumberValidator.normalizePhoneNumber(request.getPhoneNumber()));
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setCreatedBy(0L);
            user.setCreatedDate(new Date());
            if (request.getRole().equals(ModelCommon.USER)) {
                user.setRoles(Set.of(role));
                user.setPhoneNumberConfirmed(false);
                user.setTwoFactorEnable(false);
            } else if (request.getRole().equals(ModelCommon.ADMIN) || request.getRole().equals(ModelCommon.MANAGEMENT) || request.getRole().equals(ModelCommon.ULTI_MANAGER)) {
                RoleEntity userRoleUser = roleRepository.findByName(ModelCommon.USER);
                user.setRoles(Set.of(role,userRoleUser));
                user.setPhoneNumberConfirmed(true);
                user.setTwoFactorEnable(true);
            }
            user.setActive(true);
            user.setDeleted(false);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public BaseResponse registerTwoFactor(OtpValidationRequestDto requestDto) {
        boolean checkValidOTP = twilioOTPService.validateOtp(requestDto);
        if (checkValidOTP) {
            for (Map.Entry<String, RegisterRequest> entry : registerAccounts.entrySet()
            ) {
                RegisterRequest item = entry.getValue();
                if (item.getUserName().equals(requestDto.getUserName())) {
                    RegisterRequest request = new RegisterRequest();
                    request.setUserName(item.getUserName());
                    request.setPhoneNumber(item.getPhoneNumber());
                    request.setPassword(item.getPassword());
                    request.setRole(item.getRole());
                    boolean checkRegister = this.baseRegister(request);
                    if (!checkRegister) {
                        return BaseResponse.error(MessageUtil.MSG_REGISTER_FAIL);
                    }
                }
            }
            return BaseResponse.success(MessageUtil.MSG_OTP_CODE_CORRECT);
        }
        return BaseResponse.error(MessageUtil.MSG_OTP_CODE_INCORRECT);
    }

    public LoginResponse login(LoginRequest request) {
        // normalize phone number
        String normalizePhoneNumber = PhoneNumberValidator.normalizePhoneNumber(request.getPhoneNumber());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            normalizePhoneNumber,
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            return LoginResponse.error(MessageUtil.MSG_AUTHENTICATION_FAIL);
        }

        var user = userRepository.findByPhoneNumber(normalizePhoneNumber);
        if (user.isPresent()) {
            var jwtToken = jwtService.generateToken(user.get());

            revokeAllUserTokens(user.get());
            saveUserToken(user, jwtToken);

            return LoginResponse.builder()
                    .token(jwtToken)
                    .message("Get token successfully!")
                    .build();
        } else {
            return LoginResponse.error(MessageUtil.MSG_USER_BY_TOKEN_NOT_FOUND);
        }
    }

    private void saveUserToken(Optional<UserEntity> user, String jwtToken) {
        var token = TokenEntity.builder()
                .userToken(user.get())
                .token(jwtToken)
                .tokeType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(UserEntity user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
