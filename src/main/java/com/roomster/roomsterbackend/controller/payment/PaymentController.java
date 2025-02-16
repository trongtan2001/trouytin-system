package com.roomster.roomsterbackend.controller.payment;

import com.roomster.roomsterbackend.base.BaseResultWithData;
import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.payment.PaymentDto;
import com.roomster.roomsterbackend.dto.payment.PaymentResultData;
import com.roomster.roomsterbackend.dto.payment.PaymentReturnDto;
import com.roomster.roomsterbackend.dto.payment.ViewPaymentReturnDto;
import com.roomster.roomsterbackend.dto.response.VnpayPayIpnResponse;
import com.roomster.roomsterbackend.dto.response.VnpayPayResponse;
import com.roomster.roomsterbackend.service.IService.payment.IPaymentService;
import com.roomster.roomsterbackend.util.extensions.ObjectExtension;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaymentController {

    public final IPaymentService paymentService;

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_MANAGE','ROLE_ADMIN')")
    @PostMapping()
    public ResponseEntity<?> create(@RequestBody PaymentDto request, Principal connectedUser) {
        return paymentService.createPayment(request, connectedUser);
    }

    @GetMapping(value = "/vnpay-return")
    public void vnpayReturn(@ModelAttribute VnpayPayResponse response, HttpServletResponse httpServletResponse) throws IOException {
        //TODO: Đầu sử lý IPN
        BaseResultWithData<VnpayPayIpnResponse> resultIpn = paymentService.vnpayReturnIpn(response);
        if (resultIpn.getData().getRspCode().equals("00")){
            //TODO: Đầu return view
            ResponseEntity<?> responseEntity = paymentService.processVnpayPaymentReturn(response);
            String returnUrl = "";
            PaymentReturnDto model = null;
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                BaseResultWithData<PaymentResultData> convertResponse = (BaseResultWithData<PaymentResultData>) responseEntity.getBody();
                returnUrl = convertResponse.getData().getReturnUrl();
                model = convertResponse.getData().getReturnDto();

                if (returnUrl.endsWith("/")) {
                    returnUrl = returnUrl.substring(0, returnUrl.length() - 1);
                }
                String queryString = ObjectExtension.toQueryString(model);
                httpServletResponse.sendRedirect(returnUrl + "?" + queryString);
            }
        }
    }

    @GetMapping(value = "/vpn-ipn")
    public BaseResultWithData<VnpayPayIpnResponse> vnpReturnIpn(@ModelAttribute VnpayPayResponse response) {
        return paymentService.vnpayReturnIpn(response);
    }

    @GetMapping(value = "/vnpay-return-view")
    public void vnpayReturnView(@ModelAttribute ViewPaymentReturnDto response, HttpServletResponse httpServletResponse) throws IOException {
        ResponseEntity<?> responseEntity = paymentService.vnpayReturnView(response);
        BaseResponse baseResponse = (BaseResponse)responseEntity.getBody();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://trouytin.online/payment/status")
                .queryParam("message", baseResponse.getMessage())
                .queryParam("success", baseResponse.isSuccess());
        httpServletResponse.sendRedirect(builder.toUriString());}

    @GetMapping(value = "/history")
    public ResponseEntity<?> paymentHistory(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
                                            Principal connectedUser){
        Pageable pageable = PageRequest.of(page, size);
        return paymentService.paymentHistory(connectedUser, pageable);
    }
}
