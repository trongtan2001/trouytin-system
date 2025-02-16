package com.roomster.roomsterbackend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentByMonthDto {
    private Integer month;
    private BigDecimal total;
}
