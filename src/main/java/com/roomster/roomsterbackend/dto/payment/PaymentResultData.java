package com.roomster.roomsterbackend.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResultData {
    private PaymentReturnDto returnDto;
    private String returnUrl;
}
