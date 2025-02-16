package com.roomster.roomsterbackend.dto.order;

import java.math.BigDecimal;
import java.util.List;

import com.roomster.roomsterbackend.entity.OrderEntity;
import com.roomster.roomsterbackend.entity.ServiceHouseEntity;

import lombok.Data;

@Data
public class OrderPaymentDto extends OrderEntity {
	
  private BigDecimal priceService; 
  private BigDecimal priceRoom;
  private List<ServiceHouseEntity> serviceHouses;
  
}
