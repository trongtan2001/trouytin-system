package com.roomster.roomsterbackend.dto.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentDto {
    private String paymentContent = "";
    private String paymentCurrency = "vnd";
    private String paymentRefId = "ROD001";
    private BigDecimal requiredAmount;
    private Date paymentDate = new Date();
    private Date expireDate = new Date(System.currentTimeMillis() + 15 * 60 * 1000); // 15 minutes in the future
    private String paymentLanguage = "vn";
    private String merchantId = "fc324545c40144e5be6552aded90c2af";
    private String paymentDestinationId = "";
    private String signature = "";
}
