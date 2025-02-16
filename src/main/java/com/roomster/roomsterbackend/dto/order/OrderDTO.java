package com.roomster.roomsterbackend.dto.order;

import lombok.Data;

@Data
public class OrderDTO {
    String orderId;
    String roomId;
    String water;
    String electric;

}
