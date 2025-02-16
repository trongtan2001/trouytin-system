package com.roomster.roomsterbackend.base;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResult {
    private boolean success;
    private String message = "";
    private List<BaseError> errors = new ArrayList<>();
}
