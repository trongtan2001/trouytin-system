package com.roomster.roomsterbackend.base;

import com.roomster.roomsterbackend.common.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto {
    private Status status;
    private String message;
}
