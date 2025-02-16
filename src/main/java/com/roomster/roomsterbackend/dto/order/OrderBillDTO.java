package com.roomster.roomsterbackend.dto.order;

import lombok.Data;

@Data
public class OrderBillDTO {
    String orderId;
    String electricPrice;
    String houseName;
    String houseAddress;
    String roomNumber;
    String waterPrice;
    String servicePrice;
    String totalPrice;
    String totalPayment;
    String datePayment;
}
