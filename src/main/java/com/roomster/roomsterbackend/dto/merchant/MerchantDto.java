package com.roomster.roomsterbackend.dto.merchant;

import lombok.Data;

@Data
public class MerchantDto {
    private String MerchantName;
    private String MerchantWebLink;
    private String MerchantIpnUrl ;
    private String MerchantReturnUrl;
}
