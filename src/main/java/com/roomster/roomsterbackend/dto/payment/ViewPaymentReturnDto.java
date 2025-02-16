package com.roomster.roomsterbackend.dto.payment;


import lombok.Data;

@Data
public class ViewPaymentReturnDto {
    private String paymentId;
    private String signature;
    private String paymentStatus;
}
