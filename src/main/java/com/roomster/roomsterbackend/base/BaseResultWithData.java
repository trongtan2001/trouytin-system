package com.roomster.roomsterbackend.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResultWithData<T> extends BaseResult {
    private T data;

    public void Set(boolean success, String message, T data) {
        this.data = data;
        this.setSuccess(success);
        this.setMessage(message);
    }
}