package com.roomster.roomsterbackend.service.impl.twilio;

import com.roomster.roomsterbackend.common.Status;
import com.roomster.roomsterbackend.config.TwilioConfig;
import com.roomster.roomsterbackend.service.IService.twilio.ITwilioService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.Console;

@Service
public class TwilioService implements ITwilioService {

    @Autowired
    private TwilioConfig twilioConfig;

    @Override
    public ResponseEntity<?> sendSMSNotification(String message, String phoneNumber) {
        ResponseEntity<?> response = null;
        try {
            PhoneNumber to = new PhoneNumber(phoneNumber);//to
            PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber()); // from
             Message messaged = Message.creator(to, from, message).create();
            response = new ResponseEntity<>(Status.DELIVERED, HttpStatus.OK);
        } catch (Exception e) {
            response = new ResponseEntity<>(Status.FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
