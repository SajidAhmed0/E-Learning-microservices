package com.paymentms.paymentms.service;

import com.paymentms.paymentms.entities.Request;
import com.paymentms.paymentms.entities.Response;
import com.stripe.exception.StripeException;

public interface PaymentIntentService {
    public Response createPaymentIntent(Request request) throws StripeException;
}
