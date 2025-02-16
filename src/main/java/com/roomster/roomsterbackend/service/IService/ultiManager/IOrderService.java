package com.roomster.roomsterbackend.service.IService.ultiManager;


import com.roomster.roomsterbackend.dto.order.OrderDTO;
import com.roomster.roomsterbackend.dto.order.OrderStatusPaymentDto;
import com.roomster.roomsterbackend.entity.OrderEntity;
import org.springframework.http.ResponseEntity;

public interface IOrderService {

	ResponseEntity<?> findAll();

	ResponseEntity<?> getOrderById(String id);

	ResponseEntity<?> deleteOrder(String id);

	ResponseEntity<?> updateOrderWaterElectric(OrderDTO order);

	ResponseEntity<?> getTotalPaymentByMonth();

	ResponseEntity<?> checkUpdateOrAddFromMonth(String id, OrderEntity order);

	ResponseEntity<?> sendMailPayment(String roomId);

	ResponseEntity<?> updateOrderPayment(String id, OrderStatusPaymentDto order);

	ResponseEntity<?> getOrderBillById(String id);

	ResponseEntity<?> downloadExcel();
}
