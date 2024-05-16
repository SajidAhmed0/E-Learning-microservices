package com.paymentms.paymentms.controller;

import com.paymentms.paymentms.entities.Request;
import com.paymentms.paymentms.entities.Response;
import com.paymentms.paymentms.service.PaymentIntentService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payment")
public class PaymentIntentController {

    @Autowired
    private PaymentIntentService paymentIntentService;

    @PostMapping("/create-payment-intent")
    public Response createPaymentIntent(@RequestBody Request request)
            throws StripeException {
        return this.paymentIntentService.createPaymentIntent(request);
    }


}
