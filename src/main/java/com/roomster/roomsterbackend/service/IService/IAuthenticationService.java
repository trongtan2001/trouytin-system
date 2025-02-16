package com.roomster.roomsterbackend.service.IService;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.auth.LoginResponse;
import com.roomster.roomsterbackend.dto.auth.LoginRequest;
import com.roomster.roomsterbackend.dto.auth.OtpValidationRequestDto;
import com.roomster.roomsterbackend.dto.auth.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface IAuthenticationService {
    public BaseResponse register(RegisterRequest request);

    public ResponseEntity<?> registerByAdmin(RegisterRequest request);

    BaseResponse registerTwoFactor(OtpValidationRequestDto request);

    LoginResponse login(LoginRequest request);

}
