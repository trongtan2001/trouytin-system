package com.roomster.roomsterbackend.controller.auth;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.base.ResponseDto;
import com.roomster.roomsterbackend.dto.auth.*;
import com.roomster.roomsterbackend.service.IService.IAuthenticationService;
import com.roomster.roomsterbackend.service.impl.twilio.TwilioOTPService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final IAuthenticationService authenticationService;
    @Operation(
            description = "Must register by only phoneNumber: +84332101032 and Format role is [ROLE_USER, ROLE_MANAGE]",
            summary = "Endpoint For Register"
    )
    @PostMapping("/registration")
    public ResponseEntity<BaseResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @Operation(
            description = "Get OTP code from smart phone",
            summary = "Endpoint For Verification OTP code"
    )
    @PostMapping("/verification-otp")
    public ResponseEntity<BaseResponse> verificationOTP(@RequestBody OtpValidationRequestDto requestDto){
        return ResponseEntity.ok(authenticationService.registerTwoFactor(requestDto));
    }

    @Operation(
            description = "Phone Number Must have pre: +84 ",
            summary = "Endpoint For Login"
    )
    @PostMapping("/authenticate")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){

        return ResponseEntity.ok(authenticationService.login(request));
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(){
        return ResponseEntity.ok("Logout successfully!");
    }
}
