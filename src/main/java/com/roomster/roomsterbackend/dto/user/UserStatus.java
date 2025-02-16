package com.roomster.roomsterbackend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatus {
    private Long totalAccount;
    private Long percentUser;
    private Long totalUser;
    private Long percentManage;
    private Long totalManage;
    private Long percentUltiManage;
    private Long totalUltiManage;
}
