package com.roomster.roomsterbackend.service.IService.payment;

import com.roomster.roomsterbackend.dto.payment.ViewPaymentReturnDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import com.roomster.roomsterbackend.dto.payment.PaymentDto;
import com.roomster.roomsterbackend.dto.response.VnpayPayResponse;
import com.roomster.roomsterbackend.dto.response.VnpayPayIpnResponse;
import com.roomster.roomsterbackend.base.BaseResultWithData;

import java.security.Principal;

public interface IPaymentService {
    ResponseEntity<?> createPayment(PaymentDto request, Principal connectedUser);

    ResponseEntity<?>  processVnpayPaymentReturn(VnpayPayResponse response);

    BaseResultWithData<VnpayPayIpnResponse> vnpayReturnIpn(VnpayPayResponse response);

    ResponseEntity<?> vnpayReturnView(ViewPaymentReturnDto response);
    ResponseEntity<?> paymentHistory(Principal connectedUser, Pageable pageable);

    ResponseEntity<?> getAllPayment(Pageable pageable);
    ResponseEntity<?> getTotalPaymentTransactionByMonth();
}
