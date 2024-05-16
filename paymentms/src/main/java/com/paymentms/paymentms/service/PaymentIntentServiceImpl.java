package com.paymentms.paymentms.service;

import com.paymentms.paymentms.entities.Request;
import com.paymentms.paymentms.entities.Response;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

@Service
public class PaymentIntentServiceImpl implements PaymentIntentService {
    @Override
    public Response createPaymentIntent(Request request) throws StripeException {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(request.getAmount() * 100L)
                        .putMetadata("courseName",
                                request.getCourseName())
                        .setCurrency("inr")
                        .setPaymentMethod("pm_card_visa")
                        .setDescription("Payment for course - " + request.getCourseName())
                        .setCustomer(null)
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams
                                        .AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        PaymentIntent intent =
                PaymentIntent.create(params);

        return new Response(intent.getId(),
                intent.getClientSecret());
    }
}
