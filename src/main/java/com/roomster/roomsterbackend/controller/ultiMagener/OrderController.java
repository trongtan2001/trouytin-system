package com.roomster.roomsterbackend.controller.ultiMagener;

import com.roomster.roomsterbackend.dto.order.OrderDTO;
import com.roomster.roomsterbackend.dto.order.OrderStatusPaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.roomster.roomsterbackend.entity.OrderEntity;
import com.roomster.roomsterbackend.service.IService.ultiManager.IOrderService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/room-master/order")
@PreAuthorize("hasRole('ROLE_ULTI_MANAGER')")
public class OrderController {

	@Autowired
	IOrderService orderService;

	@GetMapping()
	public ResponseEntity<?> getAllorderService() {
		return orderService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getorderServiceById(@PathVariable String id) {
		return orderService.getOrderById(id);
	}

	@GetMapping("/bill/{id}")
	public ResponseEntity<?> getorderBillById(@PathVariable String id) {
		return orderService.getOrderBillById(id);
	}

	@GetMapping("/status")
	public ResponseEntity<?> getTotalPaymentByMonth() {
		return orderService.getTotalPaymentByMonth();
	}

	@PostMapping("/{id}")
	public ResponseEntity<?> createorderService(@PathVariable String id, @RequestBody OrderEntity order) {
		return orderService.checkUpdateOrAddFromMonth(id, order);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteorderService(@PathVariable String id) {
		return orderService.deleteOrder(id);
	}

	@PutMapping("water-electric")
	public ResponseEntity<?> updateorderService(@RequestBody OrderDTO order) {
		return orderService.updateOrderWaterElectric(order);
	}

	@PutMapping("/payment/{id}")
	public ResponseEntity<?> updateorderService(@PathVariable String id, @RequestBody OrderStatusPaymentDto order) {
		return orderService.updateOrderPayment(id, order);
	}

	@PostMapping("/mail-payment/{roomId}")
	public ResponseEntity<?> sendMail(@PathVariable String roomId) {
		return orderService.sendMailPayment(roomId);
	}

	@GetMapping("/download")
	public ResponseEntity<?> downloadExcel() {
		return this.orderService.downloadExcel();
	}

}