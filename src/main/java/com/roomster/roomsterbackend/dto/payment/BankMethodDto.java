package com.roomster.roomsterbackend.dto.payment;

import com.roomster.roomsterbackend.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankMethodDto {

    private Long bankMethodId;

    private String bankName;

    private String bankAccount;
}