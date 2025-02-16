package com.roomster.roomsterbackend.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roomster.roomsterbackend.entity.MerchantEntity;
import com.roomster.roomsterbackend.entity.PaymentDestinationEntity;
import com.roomster.roomsterbackend.entity.UserEntity;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDtoMapper {
    private String id;
    private String paymentContent;
    private String paymentCurrency;
    private BigDecimal requiredAmount;
    private Date paymentDate;
    private String paymentLanguage;
    private BigDecimal paidAmount;
    private String paymentStatus;
    private String paymentLastMessage;
    private String paymentDestinationsName;
    private String phoneNumberUser;
}
