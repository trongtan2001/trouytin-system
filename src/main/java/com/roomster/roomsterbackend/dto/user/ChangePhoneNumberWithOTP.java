package com.roomster.roomsterbackend.dto.user;

import lombok.Data;

@Data
public class ChangePhoneNumberWithOTP {
    private String phoneNumber;
    private String OTPNumber;
}
