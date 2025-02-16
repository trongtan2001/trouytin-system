package com.roomster.roomsterbackend.service.impl.ultiManger;

import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.order.*;
import com.roomster.roomsterbackend.entity.*;
import com.roomster.roomsterbackend.repository.BankMethodRepository;
import com.roomster.roomsterbackend.service.impl.mailService.MailService;
import com.roomster.roomsterbackend.repository.OrderRepository;
import com.roomster.roomsterbackend.repository.RoomRepository;
import com.roomster.roomsterbackend.repository.TenantRepository;
import com.roomster.roomsterbackend.service.IService.ultiManager.IOrderService;
import com.roomster.roomsterbackend.util.excel.ExcelUtil;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import com.roomster.roomsterbackend.util.validator.ValidatorUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements IOrderService {
	@Autowired
	RoomRepository roomRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	TenantRepository tenantRepository;

	@Autowired
	BankMethodRepository bankMethodRepository;

	@Autowired
	MailService mailService;

	@Override
	public ResponseEntity<?> findAll() {
		ResponseEntity<?> responseEntity;
		try {
			List<OrderEntity> orderList = orderRepository.findAll();
			orderList.sort(Comparator.comparing(OrderEntity::getPaymentDate, Comparator.reverseOrder()));
			List<OrderPaymentDto> orderPaymentDtos = new ArrayList<OrderPaymentDto>();
			for (OrderEntity order : orderList) {
				OrderPaymentDto paymentDto = new OrderPaymentDto();
				Long roomId = order.getRoomId();
				Optional<InforRoomEntity> inforRoomEntity = roomRepository.findById(roomId);
				if (inforRoomEntity.isPresent()) {
					InforRoomEntity room = inforRoomEntity.get();
					BeanUtils.copyProperties(order, paymentDto);
					paymentDto.setPriceService(order.getService());
					paymentDto.setElectricity(paymentDto.getElectricity().multiply(room.getElectricityPrice()));
					paymentDto.setWater(paymentDto.getWater().multiply(room.getWaterPrice()));
					paymentDto.setPriceRoom(room.getPrice());
					orderPaymentDtos.add(paymentDto);
				} else {
					responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			responseEntity = new ResponseEntity<>(orderPaymentDtos, HttpStatus.OK);
		} catch (Exception e) {
			responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}

	@Override
	public ResponseEntity<?> getOrderById(String id) {
		ResponseEntity<?> responseEntity;
		try {
			if (ValidatorUtils.isNumber(id)) {
				Long idL = Long.parseLong(id);
				Optional<OrderEntity> order = this.orderRepository.findById(idL);
				if (order.isPresent()) {
					responseEntity = new ResponseEntity<>(order.get(), HttpStatus.OK);
				} else {
					responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ORDER_NOT_FOUND),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ID_FORMAT_INVALID),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}

	@Override
	public ResponseEntity<?> deleteOrder(String id) {
		ResponseEntity<?> responseEntity;
		try {
			if (ValidatorUtils.isNumber(id)) {
				Long idL = Long.parseLong(id);
				Optional<OrderEntity> order = this.orderRepository.findById(idL);
				if (order.isPresent()) {
					this.orderRepository.deleteById(idL);
					responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_DELETE_SUCCESS),
							HttpStatus.OK);
				} else {
					responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ORDER_NOT_FOUND),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ID_FORMAT_INVALID),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}

	@Override
	public ResponseEntity<?> updateOrderWaterElectric(OrderDTO order) {
		OrderEntity oldOrder = null;
		ResponseEntity<?> responseEntity;
		try {
			if (ValidatorUtils.isNumber(order.getOrderId())) {
				Long idL = Long.parseLong(order.getOrderId());
				Optional<OrderEntity> Order = this.orderRepository.findById(idL);
				if (Order.isPresent()) {
					oldOrder.setElectricity(BigDecimal.valueOf(Double.parseDouble(order.getElectric())));
					oldOrder.setWater(BigDecimal.valueOf(Double.parseDouble(order.getWater())));
					oldOrder = this.orderRepository.save(oldOrder);
					responseEntity = new ResponseEntity<>(oldOrder, HttpStatus.OK);
				} else {
					responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ORDER_NOT_FOUND),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ID_FORMAT_INVALID),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}

	@Override
	public ResponseEntity<?> getTotalPaymentByMonth() {
		ResponseEntity<?> responseEntity;
		try {
			Long userId = 0L;
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserEntity) {
				userId = ((UserEntity) principal).getId();
			}
			List<Object[]> result = orderRepository.getTotalPaymentByMonth(userId);
			List<PaymentByMonthDto> paymentByMonthDtoList = result.stream()
					.map(row -> new PaymentByMonthDto((Integer) row[0], (BigDecimal) row[1]))
					.toList();
			responseEntity = new ResponseEntity<>(paymentByMonthDtoList, HttpStatus.OK);
		} catch (Exception e) {
			responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ID_FORMAT_INVALID),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}

	@Override
	public ResponseEntity<?> checkUpdateOrAddFromMonth(String id, OrderEntity order) {

		List<OrderEntity> orderCheck = orderRepository.findAll();
		LocalDate currentDate = LocalDate.now();
		int currentMonth = currentDate.getMonth().getValue() - 1;
		Optional<OrderEntity> orderOptional = orderRepository.findOrderForRoomInCurrentMonth(order.getRoomId());

		if (orderOptional.isPresent()) {
			if (orderOptional.get().getStatusPayment().equals("Y") || orderOptional.get().getStatusPayment().equals("P")) {
				return new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ORDER_PAYMENT),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return updateOrderAfterCheck(orderOptional.get().getOrderId().toString(), order);
		}
		else{
			Long idRoom = order.getRoomId();
			Optional<InforRoomEntity> room = roomRepository.findById(idRoom);
			InforRoomEntity roomService = room.get();
			if(roomService.getEmptyRoom() == 1){
				return createOrderAfterCheck(order);
			}
			else{
				return new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_IS_EMPTY),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> createOrderAfterCheck(OrderEntity order) {
		System.out.println("ADD");
		ResponseEntity<?> responseEntity;
		BigDecimal price = BigDecimal.ZERO;
		BigDecimal priceService = BigDecimal.ZERO;
		BigDecimal priceRoom = BigDecimal.ZERO;
		try {
			// check room
			Long idRoom = order.getRoomId();
			Optional<InforRoomEntity> room = roomRepository.findById(idRoom);
			InforRoomEntity roomService = room.get();
			priceRoom = priceRoom.add(roomService.getPrice());
			for (RoomServiceEntity roomServicePrice : roomService.getServices()) {
				priceService = priceService.add(roomServicePrice.getServiceHouse().getServicePrice());
			}
			order.setService(priceService);
			if (room.isPresent()) {
				// validate room
				if (order.getWater() != null && order.getElectricity() != null) {
					price = room.get().getElectricityPrice().multiply(order.getElectricity())
							.add(room.get().getWaterPrice().multiply(order.getWater()));
					order.setTotal(price.add(priceService.add(priceRoom)));
					order.setStatusPayment("N");
					LocalDate currentDate = LocalDate.now();
					order.setPaymentDate(Date.valueOf(currentDate));
				}
				order = orderRepository.save(order);
				responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_ADD_SUCCESS), HttpStatus.OK);
			} else {
				responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ROOM_NOT_FOUND),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}

	public ResponseEntity<?> updateOrderAfterCheck(String id, OrderEntity order) {
		System.out.println("UPDATE");

		ResponseEntity<?> responseEntity;
		BigDecimal price = BigDecimal.ZERO;
		BigDecimal priceService = BigDecimal.ZERO;
		BigDecimal priceRoom = BigDecimal.ZERO;
		try {
			if (ValidatorUtils.isNumber(id)) {
				Long idL = Long.parseLong(id);
				Optional<OrderEntity> existingOrderOptional = orderRepository.findById(idL);
				if (existingOrderOptional.isPresent()) {
					OrderEntity existingOrder = existingOrderOptional.get();
					Optional<InforRoomEntity> room = roomRepository.findById(order.getRoomId());
					existingOrder.setElectricity(order.getElectricity());
					existingOrder.setWater(order.getWater());
					existingOrder.setStatusPayment("N");
					LocalDate currentDate = LocalDate.now();
					existingOrder.setPaymentDate(Date.valueOf(currentDate));
					InforRoomEntity roomService = room.get();
					priceRoom = priceRoom.add(roomService.getPrice());
					for (RoomServiceEntity roomServicePrice : roomService.getServices()) {
						priceService = priceService.add(roomServicePrice.getServiceHouse().getServicePrice());
					}
					existingOrder.setService(priceService);
					if (room.isPresent()) {
						// validate room
						if (order.getWater() != null && order.getElectricity() != null) {
							price = room.get().getElectricityPrice().multiply(order.getElectricity())
									.add(room.get().getWaterPrice().multiply(order.getWater()));
							existingOrder.setTotal(price.add(priceService.add(priceRoom)));
						}
						existingOrder = orderRepository.save(existingOrder);
						return new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_UPDATE_SUCCESS), HttpStatus.OK);
					}
					responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_UPDATE_SUCCESS), HttpStatus.OK);
				} else {
					responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ORDER_NOT_FOUND),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ID_FORMAT_INVALID),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			responseEntity = new ResponseEntity<>(BaseResponse.error(e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}

	@Override
	public ResponseEntity<?> sendMailPayment(String roomId) {
		ResponseEntity<?> responseEntity;
		try{
			Long userId = 0L;
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserEntity) {
				userId = ((UserEntity) principal).getId();
			}
			var listBankMethod = bankMethodRepository.findAllByUserId(userId);
			if(listBankMethod == null || listBankMethod.isEmpty()){
				return new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_METHOD_PAYMENT_NOT_FOUND),
						HttpStatus.BAD_REQUEST);
			}
			List<OrderEntity> listOrder = orderRepository.findAllByRoomHouseUserId(userId);
			LocalDate currentDate = LocalDate.now();
			int currentMonth = currentDate.getMonth().getValue();
			List<OrderEntity> listOrderResult = listOrder.stream()
					.filter(o -> (o.getRoomId().toString().equals(roomId) && (o.getStatusPayment().trim().equals("N") || o.getStatusPayment().trim().equals("P"))))
					.toList();
			if(listOrderResult == null || listOrderResult.isEmpty()){
				return new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ORDER_PAYMENT_NOT_FOUND),
						HttpStatus.BAD_REQUEST);
			}

			for (OrderEntity order : listOrderResult) {
				List<TenantEntity> listTenant = tenantRepository.findByRoomId(order.getRoomId());
				for (TenantEntity tenant : listTenant) {
					mailService.sendSimpleEmail(tenant, "Hóa đơn thánh toán phòng trọ tháng : " + currentMonth, "", tenant.getName(), order);
				}
			}
			return new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_SEND_MAIL_SUCCESS),
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SEND_MAIL_FAILURE),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> updateOrderPayment(String id, OrderStatusPaymentDto order) {
		ResponseEntity<?> responseEntity;
		try {
			Long idL = Long.parseLong(id);
			Optional<OrderEntity> orderEntityOptional = this.orderRepository.findById(idL);
			if (orderEntityOptional.isPresent()) {
				OrderEntity orderEntity = orderEntityOptional.get();
				orderEntity.setTotalPayment(BigDecimal.valueOf(Long.parseLong(order.getBillNumber())));
				if (orderEntity.getTotalPayment().compareTo(orderEntity.getTotal()) == 0) {
					orderEntity.setStatusPayment("Y");
				} else if (orderEntity.getTotalPayment().compareTo(orderEntity.getTotal()) < 0) {
					orderEntity.setStatusPayment("P");
				} else if (orderEntity.getTotalPayment().compareTo(BigDecimal.ZERO) == 0) {
					orderEntity.setStatusPayment("N");
				}
				this.orderRepository.save(orderEntity);
				responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_UPDATE_SUCCESS),
						HttpStatus.OK);
			} else {
				responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ORDER_NOT_FOUND),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			responseEntity = new ResponseEntity<>(BaseResponse.error(e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}

	@Override
	public ResponseEntity<?> getOrderBillById(String id) {
		ResponseEntity<?> responseEntity;
		try {
			if (ValidatorUtils.isNumber(id)) {
				Long idL = Long.parseLong(id);
				Optional<OrderEntity> orderEntityOptional = this.orderRepository.findById(idL);
				if (orderEntityOptional.isPresent()) {
					OrderEntity order = orderEntityOptional.get();
					OrderBillDTO orderBillDTO = new OrderBillDTO();
					orderBillDTO.setOrderId(order.getOrderId().toString());
					orderBillDTO.setHouseName(order.getRoom().getHouse().getHouseName());
					orderBillDTO.setHouseAddress(order.getRoom().getHouse().getAddress());
					orderBillDTO.setRoomNumber(order.getRoom().getNumberRoom() + "");
					orderBillDTO.setServicePrice(order.getService().toString());
					orderBillDTO.setElectricPrice(order.getElectricity().multiply(order.getRoom().getElectricityPrice()).toString());
					orderBillDTO.setWaterPrice(order.getWater().multiply(order.getRoom().getWaterPrice()).toString());
					orderBillDTO.setTotalPayment(order.getTotalPayment().toString());
					orderBillDTO.setTotalPrice(order.getTotal().toString());
					orderBillDTO.setDatePayment(order.getPaymentDate().toString());
					responseEntity = new ResponseEntity<>(orderBillDTO, HttpStatus.OK);
				} else {
					responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ORDER_NOT_FOUND),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_ID_FORMAT_INVALID),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}

	@Override
	public ResponseEntity<?> downloadExcel() {
		ByteArrayOutputStream outputStream;
		try {
			Long userId = 0L;
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserEntity) {
				userId = ((UserEntity) principal).getId();
			}
			ExcelUtil excelUtil = new ExcelUtil(this.orderRepository.findAllOrderInCurrentMonth(userId));
			outputStream = excelUtil.export();
		} catch (IOException e) {
			return new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Chuẩn bị ResponseEntity với byte array và headers để tải xuống
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", "THONG_KE.xlsx");

		return ResponseEntity.ok()
				.headers(headers)
				.body(outputStream.toByteArray());
	}
}
