package com.roomster.roomsterbackend.dto.auth;

import lombok.Data;

@Data
public class ChangePhoneNumberRequest {
    private String oldPhoneNumber;
    private String newPhoneNumber;
    private String confirmPhoneNumber;
}
