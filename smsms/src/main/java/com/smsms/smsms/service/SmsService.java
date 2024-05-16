package com.smsms.smsms.service;

import com.smsms.smsms.entities.SmsRequest;

public interface SmsService {
    void sendSms(SmsRequest smsRequest);
}
