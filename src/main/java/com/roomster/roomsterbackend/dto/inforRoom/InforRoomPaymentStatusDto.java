package com.roomster.roomsterbackend.dto.inforRoom;

import com.roomster.roomsterbackend.dto.order.OrderStatusPaymentDto;
import lombok.Data;

import java.util.List;

@Data
public class InforRoomPaymentStatusDto {
    String houseName;
    String roomName;
    List<OrderStatusPaymentDto> orderStatusPayments;
}
