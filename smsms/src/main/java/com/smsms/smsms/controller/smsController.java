package com.smsms.smsms.controller;

import com.smsms.smsms.entities.SmsRequest;
import com.smsms.smsms.service.NotifySmsServiceImpl;
import com.smsms.smsms.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sms")
public class smsController {

    private SmsService smsService = new NotifySmsServiceImpl();

    @PostMapping("send")
    public void sendSms(@RequestBody SmsRequest smsRequest){
        smsService.sendSms(smsRequest);
    }
}
