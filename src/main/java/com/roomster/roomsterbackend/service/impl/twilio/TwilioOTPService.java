package com.roomster.roomsterbackend.service.impl.twilio;

import com.roomster.roomsterbackend.config.TwilioConfig;
import com.roomster.roomsterbackend.dto.auth.OtpRequestDto;
import com.roomster.roomsterbackend.dto.auth.OtpValidationRequestDto;
import com.roomster.roomsterbackend.base.ResponseDto;
import com.roomster.roomsterbackend.common.Status;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class TwilioOTPService {
    @Autowired
    private TwilioConfig twilioConfig;

    private final Map<String, String> otpMap = new HashMap<>();


    public ResponseDto sendSMS(OtpRequestDto otpRequest) {
        ResponseDto otpResponseDto = null;
        try {
            PhoneNumber to = new PhoneNumber(otpRequest.getPhoneNumber());//to
            PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber()); // from
            String otp = generateOTP();
            String otpMessage = "Kính gửi Quý khách hàng, Mã OTP của bạn là " + otp + " để xác nhận. Cảm ơn bạn.";
            Message message = Message.creator(to, from, otpMessage).create();
            otpMap.put(otpRequest.getUserName(), otp);
            otpResponseDto = new ResponseDto(Status.DELIVERED, MessageUtil.MSG_OTP_DELIVERED);
        } catch (Exception e) {
            otpResponseDto = new ResponseDto(Status.FAILED, MessageUtil.MSG_OTP_FAILED);
        }
        return otpResponseDto;
    }

    public boolean validateOtp(OtpValidationRequestDto otpValidationRequest) {
        String userName = otpValidationRequest.getUserName();
        String storedOtp = otpMap.get(userName);

        if (storedOtp != null && storedOtp.equals(otpValidationRequest.getOtpNumber())) {
            // OTP is valid
            otpMap.remove(userName);
            return true;
        } else {
            // OTP is invalid
            return false;
        }
    }

    //    generation OTP
    private String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }
}
