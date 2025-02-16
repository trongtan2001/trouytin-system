package com.roomster.roomsterbackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String message;

    public static LoginResponse error(String errorMessage) {
        LoginResponse response = new LoginResponse();
        response.setMessage(errorMessage);
        return response;
    }
}
