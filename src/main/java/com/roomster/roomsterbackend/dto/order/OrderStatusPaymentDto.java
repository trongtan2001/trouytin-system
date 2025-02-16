package com.roomster.roomsterbackend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusPaymentDto {
    private String date;
    private String billNumber;
    private String paymentNumber;
    private String restNumber;
}
