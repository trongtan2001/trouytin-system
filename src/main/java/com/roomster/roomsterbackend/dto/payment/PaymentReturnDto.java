package com.roomster.roomsterbackend.dto.payment;

import lombok.Data;
/**
 * The PaymentReturnDto to return result to FE
 * comments.
 */
@Data
public class PaymentReturnDto {
    private String paymentId;
    /// <summary>
    /// 00: Success
    /// 99: Unknown
    /// 10: Error
    /// </summary>
    private String paymentStatus;
    private String paymentMessage;
    /// <summary>
    /// Format: yyyyMMddHHmmss
    /// </summary>
    private String paymentDate;
    private String paymentRefId;
    private String amount;
    private String signature;
}
