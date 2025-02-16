package com.roomster.roomsterbackend.service.impl.payment;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.roomster.roomsterbackend.base.BaseResultWithData;
import com.roomster.roomsterbackend.base.BaseResultWithDataAndCount;
import com.roomster.roomsterbackend.config.VnpayConfig;
import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.order.PaymentByMonthDto;
import com.roomster.roomsterbackend.dto.payment.*;
import com.roomster.roomsterbackend.dto.request.VnpayPayRequest;
import com.roomster.roomsterbackend.dto.response.VnpayPayIpnResponse;
import com.roomster.roomsterbackend.dto.response.VnpayPayResponse;
import com.roomster.roomsterbackend.entity.*;
import com.roomster.roomsterbackend.mapper.PaymentMapper;
import com.roomster.roomsterbackend.repository.UserRepository;
import com.roomster.roomsterbackend.repository.payment.*;
import com.roomster.roomsterbackend.service.IService.payment.IPaymentService;
import com.roomster.roomsterbackend.service.IService.payment.IUserIpAddress;
import com.roomster.roomsterbackend.util.extensions.ConvertObjectToJsonExtension;
import com.roomster.roomsterbackend.util.helpers.HashHelper;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PaymentService implements IPaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentSignatureRepository paymentSignatureRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private PaymentDestinationRepository paymentDestinationRepository;

    @Autowired
    private VnpayConfig vnpayConfig;

    @Autowired
    private IUserIpAddress ipAddress;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public ResponseEntity<?> createPayment(PaymentDto request, Principal connectedUser) {
        ResponseEntity<?> response = null;
        try {
            var user = (UserEntity) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            var resutl = new BaseResultWithData<PaymentLinkDto>();

            //TODO: insert payment
            PaymentEntity payment = new PaymentEntity();
            payment.setId(HashHelper.generateEntityId());
            payment.setPaymentContent(request.getPaymentContent());
            payment.setPaymentCurrency(request.getPaymentCurrency());
            payment.setPaymentRefId(request.getPaymentRefId());
            payment.setRequiredAmount(request.getRequiredAmount());
            payment.setPaymentDate(request.getPaymentDate());
            payment.setExpireDate(request.getExpireDate());
            payment.setPaymentLanguage(request.getPaymentLanguage());

            //TODO: Find merchant by id
            Optional<MerchantEntity> merchant = merchantRepository.findById(request.getMerchantId());
            merchant.ifPresent(payment::setMerchant);
            //TODO: Find payment destination by id
            Optional<PaymentDestinationEntity> paymentDestination = paymentDestinationRepository.findById(request.getPaymentDestinationId());
            paymentDestination.ifPresent(payment::setPaymentDestinations);

            //TODO: save userId base on token
            if (user != null) {
                payment.setUserPayment(user);
            }

            PaymentEntity paymentResult = paymentRepository.save(payment);

            PaymentSignatureEntity paymentSignature = new PaymentSignatureEntity();
            paymentSignature.setId(HashHelper.generateEntityId());
            // get merchant id to set signature of owner
            paymentSignature.setSignOwn(request.getMerchantId());
            paymentSignature.setPaymentSignature(paymentResult);
            // get username of account to set signature
            if (user != null) {
                //TODO: insert payment signature
                paymentSignature.setSignValue(user.getUsername());
                paymentSignature.setIsValid(true);
            } else {
                paymentSignature.setIsValid(false);
            }
            paymentSignatureRepository.save(paymentSignature);

            //TODO: Get link to process transaction payment
            var paymentUrl = "";
            switch (request.getPaymentDestinationId()) {
                case "VNPAY":
                    var vnpayPayRequest = new VnpayPayRequest(
                            vnpayConfig.getVersion(),
                            vnpayConfig.getTmnCode(),
                            LocalDateTime.now(),
                            ipAddress.getIpAddress(),
                            request.getRequiredAmount(),
                            request.getPaymentCurrency(),
                            "other",
                            request.getPaymentContent(),
                            vnpayConfig.getReturnUrl(),
                            paymentResult.getId());
                    paymentUrl = vnpayPayRequest.getLink(vnpayConfig.getPaymentUrl(), vnpayConfig.getHashSecret());
                    break;
                default:
                    break;
            }

            PaymentLinkDto paymentLinkDto = PaymentLinkDto.builder().paymentId(payment.getId()).paymentUrl(paymentUrl).build();
            resutl.Set(true, MessageUtil.MSG_OK, paymentLinkDto);

            response = new ResponseEntity<>(resutl, HttpStatus.OK);

        } catch (Exception ex) {
            response = new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Function: To process data from vnpay return to
     **/
    @Override
    public ResponseEntity<?> processVnpayPaymentReturn(VnpayPayResponse response) {
        String returnUrl = "";
        var result = new BaseResultWithData<PaymentResultData>();
        try {
//            url return of merchant
            var resultData = new PaymentReturnDto();

            //TODO: check data response
            var isValidSignature = response.isValidSignature(vnpayConfig.getHashSecret());

            if (isValidSignature) {
                Optional<PaymentEntity> payment = paymentRepository.findById(response.getVnp_TxnRef());
                if (payment.isPresent()) {
                    Optional<MerchantEntity> merchant = merchantRepository.findById(payment.get().getMerchant().getId());
                    if (merchant.isPresent()) {
                        returnUrl = merchant.get().getMerchantReturnUrl();
                    }
                } else {
                    resultData.setPaymentStatus("11");
                    resultData.setPaymentMessage("Can't find payment at payment service");
                }

                if (Objects.equals(response.getVnp_ResponseCode(), "00")) {
                    resultData.setPaymentStatus("00");
                    resultData.setPaymentId(payment.get().getId());

                    //TODO: MAKE SIGNATURE
                    resultData.setSignature(HashHelper.generateEntityId());
                } else {
                    resultData.setPaymentStatus("10");
                    resultData.setPaymentMessage("Payment process failed");
                }

                result.setSuccess(true);
                result.setMessage("OK");

                PaymentResultData paymentResultData = PaymentResultData.builder().returnDto(resultData).returnUrl(returnUrl).build();
                result.setData(paymentResultData);

            } else {
                resultData.setPaymentStatus("99");  // signature is not true
                resultData.setPaymentMessage("Invalid Signature in response");
            }
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /***
     * lay data vnpay tra ve url cua ipn de su ly phia BE va DB
     * insert table payment transaction
     * update table payment
     * return for vnpay
     * */
    @Override
    public BaseResultWithData<VnpayPayIpnResponse> vnpayReturnIpn(VnpayPayResponse response) {
        var result = new BaseResultWithData<VnpayPayIpnResponse>();
        var resultData = new VnpayPayIpnResponse();
        String message = "";
        String status = "";
        try {
            // Kiem tra chu ki
            var isValidSignature = response.isValidSignature(vnpayConfig.getHashSecret());
            if (isValidSignature) {
                // Get payment by id
                Optional<PaymentEntity> payment = paymentRepository.findById(response.getVnp_TxnRef());
                if (payment.isPresent()) {
                    // check neu so tien vnpay tra ve == so tien yeu cau thanh toan
                    BigDecimal responseAmount = response.getVnp_Amount().divide(new BigDecimal(100));
                    if (payment.get().getRequiredAmount().compareTo(responseAmount) == 0) {
                        // check if giao dich chua hoan tat
                        if (!Objects.equals(payment.get().getPaymentStatus(), "0")) {
                            // kiem tra vpm_response code tra ve giao dich thanh cong hay khong
                            if (Objects.equals(response.getVnp_ResponseCode(), "00") && Objects.equals(response.getVnp_TransactionStatus(), "00")) {
                                status = "0";
                                message = "Transaction Success";
                            } else {
                                status = "-1";
                                message = "Transaction error";
                            }
                            //TODO: insert payment transaction table
                            PaymentTransactionEntity paymentTransactionRequestDb = new PaymentTransactionEntity();
                            paymentTransactionRequestDb.setId(HashHelper.generateEntityId());
                            paymentTransactionRequestDb.setTranMessage(message);
                            String payLoad = ConvertObjectToJsonExtension.convertToJson(response);
                            paymentTransactionRequestDb.setTranPayload(payLoad);
                            paymentTransactionRequestDb.setTranStatus(status);
                            paymentTransactionRequestDb.setTranAmount(response.getVnp_Amount().divide(new BigDecimal(100)));
                            paymentTransactionRequestDb.setTranDate(new Date());
                            paymentTransactionRequestDb.setCreatedBy(payment.get().getUserPayment().getId());
                            paymentTransactionRequestDb.setCreatedDate(new Date());
                            paymentTransactionRequestDb.setPaymentTransaction(payment.get());

                            PaymentTransactionEntity paymentTransactionRes = paymentTransactionRepository.save(paymentTransactionRequestDb);

                            //TODO: Update payment table
                            payment.get().setPaidAmount(response.getVnp_Amount().divide(new BigDecimal(100)));
                            payment.get().setPaymentLastMessage(message);
                            payment.get().setPaymentStatus(status);
                            resultData.Set("00", "Giao dịch thành công");
                            paymentRepository.save(payment.get());
                        } else {
                            resultData.Set("02", "Order already confirmed");
                        }
                    } else {
                        resultData.Set("04", "Invalid amount");
                    }
                } else {
                    resultData.Set("01", "Order not found");
                }
            } else {
                resultData.Set("97", "Invalid signature");
            }

        } catch (Exception ex) {
            resultData.Set("99", "Input required data");
        }
        result.setData(resultData);
        return result;
    }

    /**
     * if response return vnp_status == '00' -> thành công - > up balance account
     **/
    @Override
    public ResponseEntity<?> vnpayReturnView(ViewPaymentReturnDto response) {
        ResponseEntity<?> responseEntity = null;
        try {
            if (Objects.equals(response.getPaymentStatus(), "00")) {
                Optional<PaymentEntity> payment = paymentRepository.findById(response.getPaymentId());
                if (payment.isPresent()) {
                    Optional<UserEntity> user = userRepository.findById(payment.get().getUserPayment().getId());
                    if (user.isPresent()) {
                        //TODO: NEED CHECK MORE CONDITIONAL FOR PAYMENT STATUS, SHOULD CHANGE TO GET PAYMENT_PAID_AMOUNT FOR CORRECT
                        if (user.get().getBalance() == null) {
                            user.get().setBalance(new BigDecimal(0));
                        }
                        BigDecimal newBalance = user.get().getBalance().add(payment.get().getPaidAmount());
                        user.get().setBalance(newBalance);
                        userRepository.save(user.get());
                        responseEntity = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_PROCESS_TRANSACTION_PAYMENT_SUCCESS), HttpStatus.OK);
                    }
                } else {
                    responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_PAYMENT_NOT_FOUND), HttpStatus.NOT_FOUND);
                }
            } else {
                responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_PROCESS_TRANSACTION_PAYMENT_FAIL), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            responseEntity = new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> paymentHistory(Principal connectedUser, Pageable pageable) {
        ResponseEntity<?> responseEntity = null;
        try {
            var user = (UserEntity) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            if (user != null) {
                List<PaymentDtoMapper> paymentDtoMappers =
                        paymentRepository.findAllByUserPayment_IdOrderByCreatedDate(user.getId(), pageable)
                                .stream()
                                .map(paymentEntity -> paymentMapper.entityToDto(paymentEntity))
                                .collect(Collectors.toList());
                Long count = paymentRepository.countPaymentEntitiesByUserPayment_Id(user.getId());
                BaseResultWithDataAndCount<List<PaymentDtoMapper>> resultData = new BaseResultWithDataAndCount<>();


                resultData.setData(paymentDtoMappers);
                resultData.setCount(count);


                responseEntity = new ResponseEntity<>(resultData, HttpStatus.OK);
            } else {
                responseEntity = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_USER_BY_TOKEN_NOT_FOUND), HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            responseEntity = new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<?> getAllPayment(Pageable pageable) {
        ResponseEntity<?> response = null;
        BaseResultWithDataAndCount<List<PaymentDtoMapper>> resultWithDataAndCount = new BaseResultWithDataAndCount<>();
        try {
            List<PaymentDtoMapper> paymentDtoMappers = paymentRepository.findAll(pageable)
                    .stream()
                    .map(paymentEntity -> paymentMapper.entityToDto(paymentEntity))
                    .collect(Collectors.toList());
            Long count = paymentRepository.count();
            resultWithDataAndCount.set(paymentDtoMappers, count);
            response = new ResponseEntity<>(resultWithDataAndCount, HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Override
    public ResponseEntity<?> getTotalPaymentTransactionByMonth() {
        ResponseEntity<?> response = null;
        BaseResultWithDataAndCount<List<PaymentDtoMapper>> resultWithDataAndCount = new BaseResultWithDataAndCount<>();
        try {
            List<Object[]> result = paymentRepository.getTotalPaymentTransactionByMonth();
            List<PaymentByMonthDto> paymentByMonthDtos = result.stream()
                    .map(row -> new PaymentByMonthDto((Integer) row[0], (BigDecimal) row[1]))
                    .collect(Collectors.toList());
            response = new ResponseEntity<>(paymentByMonthDtos, HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
