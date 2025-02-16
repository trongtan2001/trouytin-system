package com.roomster.roomsterbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VnpayPayIpnResponse {
    public String RspCode = "";
    public String Message = "";

    public void Set(String rspCode, String message)
    {
        RspCode = rspCode;
        Message = message;
    }
}
