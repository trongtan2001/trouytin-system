package com.roomster.roomsterbackend.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDtoWithFilter {
    private Long id;
    private String title;
    private String address;
    private Date createdDate;
    private Date modifiedDate;
    private BigDecimal price;
    private boolean isDeleted;
    private String status;
    private String image;
    private double acreage;
}
