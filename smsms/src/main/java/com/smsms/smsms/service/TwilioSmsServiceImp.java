package com.smsms.smsms.service;

import com.smsms.smsms.configuration.TwilioConfiguration;
import com.smsms.smsms.entities.SmsRequest;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsServiceImp implements SmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwilioSmsServiceImp.class);

    private final TwilioConfiguration twilioConfiguration;

    @Autowired
    public TwilioSmsServiceImp(TwilioConfiguration twilioConfiguration) {
        this.twilioConfiguration = twilioConfiguration;
    }

    @Override
    public void sendSms(SmsRequest smsRequest) {
        if(isPhoneNumberValid(smsRequest.getPhoneNumber())){

            PhoneNumber to = new PhoneNumber(smsRequest.getPhoneNumber());
            PhoneNumber from = new PhoneNumber(twilioConfiguration.getTrialNumber());
            LOGGER.info("from phone {}", from);
            LOGGER.info("to phone {}", to);
            LOGGER.info("sms {}", smsRequest);
            String message = smsRequest.getMessage();
            MessageCreator creator = Message.creator(to, from, message);
            creator.create();
            LOGGER.info("sent sms : {}", smsRequest);
        }else {
            throw new IllegalArgumentException("Phone number [" + smsRequest.getPhoneNumber() + "] is not a valid number");
        }

    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        //TODO: implement validation for phone number
        return true;
    }
}
