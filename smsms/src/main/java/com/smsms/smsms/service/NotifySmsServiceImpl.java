package com.smsms.smsms.service;

import com.smsms.smsms.entities.SmsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;


public class NotifySmsServiceImpl implements SmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifySmsServiceImpl.class);
    @Override
    public void sendSms(SmsRequest smsRequest) {

        String destNumber = smsRequest.getPhoneNumber();
        String msg = smsRequest.getMessage();

        //sajid
//        String user_id = "27120";
//        String api_key = "vF2J7cVOOsmMJWkeuSuh";
//        String sender_id = "NotifyDEMO";

        //sudhais
        String user_id = "27170";
        String api_key = "DunGgQgDGyTj1RlMfzVe";
        String sender_id = "NotifyDEMO";

        if(isPhoneNumberValid(destNumber)){
            String uri = "https://app.notify.lk/api/v1/send?user_id=" + user_id + "&api_key=" + api_key + "&sender_id=" + sender_id + "&to=" + destNumber + "&message=" + msg;

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            LOGGER.info("sent sms : {}", result);
        }else {
            throw new IllegalArgumentException("Phone number [" + smsRequest.getPhoneNumber() + "] is not a valid number");
        }
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        //TODO: implement validation for phone number
        return true;
    }
}
