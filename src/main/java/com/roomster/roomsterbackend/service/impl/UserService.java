package com.roomster.roomsterbackend.service.impl;

import com.cloudinary.Cloudinary;
import com.roomster.roomsterbackend.base.BaseResultWithDataAndCount;
import com.roomster.roomsterbackend.common.ModelCommon;
import com.roomster.roomsterbackend.common.Status;
import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.base.ResponseDto;
import com.roomster.roomsterbackend.dto.auth.*;
import com.roomster.roomsterbackend.dto.user.ChangePhoneNumberWithOTP;
import com.roomster.roomsterbackend.dto.user.UpdateProfileRequest;
import com.roomster.roomsterbackend.dto.user.UserDto;
import com.roomster.roomsterbackend.dto.user.UserStatus;
import com.roomster.roomsterbackend.entity.RoleEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import com.roomster.roomsterbackend.mapper.UserMapper;
import com.roomster.roomsterbackend.repository.RoleRepository;
import com.roomster.roomsterbackend.repository.UserRepository;
import com.roomster.roomsterbackend.service.IService.IUserService;
import com.roomster.roomsterbackend.service.impl.twilio.TwilioOTPService;
import com.roomster.roomsterbackend.util.handler.TokenHandler;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import com.roomster.roomsterbackend.util.validator.PhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TwilioOTPService twilioOTPService;

    @Autowired
    private RoleRepository roleRepository;

    private final JwtService jwtService;

    @Autowired
    private TokenHandler tokenHandler;
    //
    private UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final Cloudinary cloudinary;

    @Autowired
    public UserService(JwtService jwtService, @Lazy UserMapper userMapper, Cloudinary cloudinary) {
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.cloudinary = cloudinary;
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public ResponseEntity<?> findAllByIsDeletedIsFalse(Pageable pageable) {
        ResponseEntity<?> response = null;
        BaseResultWithDataAndCount<List<UserDto>> resultWithDataAndCount = new BaseResultWithDataAndCount<>();
        try {
            List<UserDto> userDtoList = userRepository.findAllByIsDeletedFalse(pageable)
                    .stream()
                    .map(userEntity -> userMapper.entityToDto(userEntity))
                    .filter(userDto -> !userDto.getUserName().equals("Admin"))
                    .collect(Collectors.toList());
            Long count = userRepository.countAllByIsDeletedFalse();
            resultWithDataAndCount.set(userDtoList, count);
            response = new ResponseEntity<>(resultWithDataAndCount, HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(MessageUtil.MSG_SYSTEM_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Override
    public ResponseEntity<?> findAllByIsDeletedIsTrue(Pageable pageable) {
        ResponseEntity<?> response = null;
        BaseResultWithDataAndCount<List<UserDto>> resultWithDataAndCount = new BaseResultWithDataAndCount<>();
        try {
            List<UserDto> userDtoList = userRepository.findAllByIsDeletedTrue(pageable)
                    .stream()
                    .map(userEntity -> userMapper.entityToDto(userEntity))
                    .collect(Collectors.toList());
            Long count = userRepository.countAllByIsDeletedTrue();
            resultWithDataAndCount.set(userDtoList, count);
            response = new ResponseEntity<>(resultWithDataAndCount, HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(MessageUtil.MSG_SYSTEM_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Override
    public Optional<UserEntity> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public UserDto viewProfile(Principal connectedUser) {
        var userEntity = (UserEntity) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return userMapper.entityToDto(userEntity);
    }

    @Override
    public BaseResponse updateProfile(UpdateProfileRequest profileRequest, MultipartFile images, Principal connectedUser) throws IOException {

        var userEntity = (UserEntity) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        userEntity.setUserName(profileRequest.getUserName());
        userEntity.setEmail(profileRequest.getEmail());
        userEntity.setDateOfBirth(profileRequest.getDateOfBirth());
        userEntity.setAddress(profileRequest.getAddress());
        if (images != null) {
            userEntity.setImages(getFileUrls(images));
        }
        userRepository.save(userEntity);
        return BaseResponse.success("Cập nhật thông tin thành công!");
    }

    @Override
    public BaseResponse changePassword(ChangePasswordRequest changePasswordRequest, Principal connectedUser) {
        var user = (UserEntity) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            return BaseResponse.error("Password is invalid!");
        }
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmationPassword())) {
            return BaseResponse.error("Confirmation password is invalid!");
        }
        user.setPasswordHash(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        return BaseResponse.success("Update password successfully");
    }

    @Override
    public UserDto getUserById(Long userId) {
        Optional<UserEntity> userEntity = userRepository.getUserEntityByUserId(userId).filter(user -> !user.isDeleted());
        return userEntity.map(user -> userMapper.entityToDto(user)).orElse(null);
    }

    @Override
    public ResponseEntity<?> upRoleToManage(OtpValidationRequestDto otpValidationRequestDto, Principal connectedUser) {
        ResponseEntity<?> responseEntity = null;
        try {
            boolean isCorrectOTPCode = twilioOTPService.validateOtp(otpValidationRequestDto);
            if (isCorrectOTPCode) {
                var user = (UserEntity) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
                RoleEntity role = roleRepository.findByName("ROLE_MANAGE");
                if (role != null) {
                    // user.setRoles(Set.of(role));
                    user.getRoles().add(role);
                    user.setPhoneNumberConfirmed(true);
                    userRepository.save(user);
                    // get new token
                    var jwtToken = jwtService.generateToken(user);

                    tokenHandler.revokeAllUserTokens(user);
                    tokenHandler.saveUserToken(Optional.of(user), jwtToken);

                    LoginResponse authenticationResponse = LoginResponse.builder()
                            .token(jwtToken)
                            .message("Get token successfully!")
                            .build();
                    return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
                }
                return new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROLE_NOT_FOUND), HttpStatus.NOT_FOUND);
            }
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_OTP_CODE_INCORRECT), HttpStatus.BAD_REQUEST);

        } catch (Exception ex) {
            responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @Override
    public ResponseEntity<?> sendOTP(OtpRequestDto requestDto) {
        ResponseEntity<?> response = null;
        try {
            ResponseDto result = twilioOTPService.sendSMS(requestDto);
            response = new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Override
    public ResponseEntity<?> changePhoneNumber(ChangePhoneNumberRequest request, Principal connectedUser) {
        ResponseEntity<?> response = null;
        try {
            String normalizePhoneNumber = PhoneNumberValidator.normalizePhoneNumber(request.getOldPhoneNumber());
            Optional<UserEntity> user = userRepository.findByPhoneNumber(normalizePhoneNumber);
            if (user.isPresent()) {
                if (request.getNewPhoneNumber().equals(request.getConfirmPhoneNumber())) {
                    if (!userRepository.existsByPhoneNumber(PhoneNumberValidator.normalizePhoneNumber(request.getNewPhoneNumber()))) {
                        //TODO: MANAGE OR ADMIN CHANGE
                        for (RoleEntity role : user.get().getRoles()
                        ) {
                            if (role.getName().equals(ModelCommon.MANAGEMENT) || role.getName().equals(ModelCommon.ADMIN)) {
                                //TODO: Check OTP of New PhoneNumber
                                OtpRequestDto otpRequest = new OtpRequestDto(user.get().getUsername(), PhoneNumberValidator.normalizePhoneNumber(request.getNewPhoneNumber()));
                                ResponseDto responseDto = twilioOTPService.sendSMS(otpRequest);
                                if (responseDto.getStatus().equals(Status.DELIVERED)) {
                                    return response = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_OTP_DELIVERED), HttpStatus.OK);
                                } else {
                                    return response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_OTP_FAILED), HttpStatus.BAD_REQUEST);
                                }
                            }
                        }
                        //TODO: USER CHANGE
                        user.get().setPhoneNumber(PhoneNumberValidator.normalizePhoneNumber(request.getNewPhoneNumber()));
                        user.get().setPhoneNumberConfirmed(false);
                        userRepository.save(user.get());
                        response = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_UPDATE_SUCCESS), HttpStatus.OK);

                    } else {
                        response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_PHONE_NUMBER_IS_EXITED), HttpStatus.BAD_REQUEST);
                    }

                } else {
                    response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_PHONE_NUMBER_CONFIRM_NOT_VALID), HttpStatus.NOT_FOUND);
                }

            } else {
                response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_PHONE_NUMBER_NOT_FOUND), HttpStatus.NOT_FOUND);
            }

        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Pre: changePhoneNumber()
     * Change phoneNumber with OTP
     **/
    @Override
    public ResponseEntity<?> changePhoneNumberWithOTP(ChangePhoneNumberWithOTP request, Principal connectedUser) {
        ResponseEntity<?> response = null;
        try {
            var user = (UserEntity) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            if (user != null) {
                boolean isCorrectOTP = twilioOTPService.validateOtp(new OtpValidationRequestDto(user.getUsername(), request.getOTPNumber()));
                if (isCorrectOTP) {
                    user.setPhoneNumber(PhoneNumberValidator.normalizePhoneNumber(request.getPhoneNumber()));
                    userRepository.save(user);
                    response = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_UPDATE_SUCCESS), HttpStatus.OK);
                } else {
                    response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_OTP_CODE_INCORRECT), HttpStatus.BAD_REQUEST);
                }
            } else {
                response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_USER_BY_TOKEN_NOT_FOUND), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Override
    public ResponseEntity<?> findAllAccountByRoleName(Pageable pageable, String roleName) {
        ResponseEntity<?> response = null;
        BaseResultWithDataAndCount<List<UserDto>> resultWithDataAndCount = new BaseResultWithDataAndCount<>();
        try {
            List<UserDto> userDtos = userRepository.findAllByRoles_NameAndIsDeletedFalse(roleName)
                    .stream()
                    .map(userEntity -> userMapper.entityToDto(userEntity))
                    .filter(userDto -> !userDto.getUserName().equals("Admin"))
                    .collect(Collectors.toList());
            Long count = userRepository.countByRoles_NameAndIsDeletedFalse(roleName);
            resultWithDataAndCount.set(userDtos, count);
            response = new ResponseEntity<>(resultWithDataAndCount, HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Override
    public ResponseEntity<?> deleteUserByIds(Long[] listId) {
        ResponseEntity<?> response = null;
        long count = 0L;
        try {
            for (Long item : listId
            ) {
                Optional<UserEntity> user = userRepository.findById(item);
                user.ifPresent(userEntity -> userEntity.setDeleted(true));
                count++;
            }
            response = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_DELETE_SUCCESS + " " + count + " tài khoản."), HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Override
    public ResponseEntity<?> upRoleUserByAdmin(String roleName, Long userId) {
        ResponseEntity<?> response = null;
        try {
            Optional<UserEntity> user = userRepository.findById(userId);
            if (user.isPresent()) {
                Optional<RoleEntity> role = Optional.of(roleRepository.findByName(roleName));
                if (role.isPresent()) {
                    user.get().getRoles().add(role.get());
                    userRepository.save(user.get());
                    response = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_UPDATE_SUCCESS), HttpStatus.OK);
                } else {
                    response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROLE_NOT_FOUND), HttpStatus.NOT_FOUND);
                }
            } else {
                response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_USER_NOT_FOUND), HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
    @Override
    public ResponseEntity<?> getUserAccountStatus() {
        try {
            RoleEntity role = roleRepository.findByName(ModelCommon.ADMIN);
            List<UserEntity> allUsers = userRepository.findAllByRolesNotContainingAndIsDeletedFalse(role);
            Long totalRoles = allUsers.stream()
                    .flatMap(user -> user.getRoles().stream())
                    .distinct()
                    .count();

            if (totalRoles == 0) {
                UserStatus userStatus = new UserStatus(0L, 0L, 0L, 0L, 0L, 0L, 0L);
                return ResponseEntity.ok(userStatus);
            }

            Map<String, Long> roleCounts = allUsers.stream()
                    .flatMap(user -> user.getRoles().stream())
                    .collect(Collectors.groupingBy(RoleEntity::getName, Collectors.counting()));

            Long totalUsers = (long) allUsers.size();

            Long percentUser = roleCounts.getOrDefault(ModelCommon.USER, 0L) * 100 / totalUsers;
            Long percentManage = roleCounts.getOrDefault(ModelCommon.MANAGEMENT, 0L) * 100 / totalUsers;
            Long percentUltiManage = roleCounts.getOrDefault(ModelCommon.ULTI_MANAGER, 0L) * 100 / totalUsers;

            UserStatus userStatus = new UserStatus(
                    totalUsers, percentUser, roleCounts.getOrDefault(ModelCommon.USER, 0L),
                    percentManage, roleCounts.getOrDefault(ModelCommon.MANAGEMENT, 0L),
                    percentUltiManage, roleCounts.getOrDefault(ModelCommon.ULTI_MANAGER, 0L)
            );

            return ResponseEntity.ok(userStatus);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR));
        }
    }

    private String getFileUrls(MultipartFile multipartFile) throws IOException {
        return cloudinary.uploader()
                .upload(multipartFile.getBytes(), Map.of("public_id", UUID.randomUUID().toString()))
                .get("url")
                .toString();
    }
}