package com.roomster.roomsterbackend.dto.inforRoom;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InforRoomDto {
    private Long inforRoomId;
    private int numberRoom;
    private int emptyRoom;
    private int stayMax;
    private double acreage;
    private BigDecimal price;
    private BigDecimal electricityPrice;
    private BigDecimal waterPrice;
}
