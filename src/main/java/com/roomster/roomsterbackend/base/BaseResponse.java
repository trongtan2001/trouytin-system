package com.roomster.roomsterbackend.base;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    private boolean success;
    private String message;

    public static BaseResponse success(String message) {
        return new BaseResponse(true, message);
    }

    public static BaseResponse error(String message) {
        return new BaseResponse(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
