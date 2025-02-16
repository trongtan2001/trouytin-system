package com.roomster.roomsterbackend.dto.service.servicePackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartServicePackage {
    private Long servicePackageId;

    private String servicePackageName;

    private Integer durationDays;

    private BigDecimal price;

    private Date createdDate;
}
