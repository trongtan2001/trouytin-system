package com.roomster.roomsterbackend.dto.service.transaction;

import com.roomster.roomsterbackend.dto.service.servicePackage.PartServicePackage;
import com.roomster.roomsterbackend.dto.user.PartUser;
import com.roomster.roomsterbackend.entity.ServicePackageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private Long transactionId;

    private Date purchaseDate;

    private Date expirationDate;

    private Integer extensionDays;

    private boolean expired;

    private PartUser partUser;

    private PartServicePackage partServicePackage;
}
