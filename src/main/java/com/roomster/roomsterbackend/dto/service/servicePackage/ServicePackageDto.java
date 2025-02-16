package com.roomster.roomsterbackend.dto.service.servicePackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicePackageDto {

    private Long servicePackageId;

    private String name;

    private String description;

    private Integer durationDays;

    private BigDecimal price;

    private Date createdDate;

    private Date modifiedDate;

    private Long modifiedBy;
}
