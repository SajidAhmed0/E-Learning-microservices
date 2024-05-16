package com.onlineCourse.controller;

import com.onlineCourse.entities.Request;
import com.onlineCourse.entities.Response;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class PaymentIntentController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @PostMapping("/create-payment-intent")
    public Response createPaymentIntent(@RequestBody Request request)
            throws StripeException {
        return  webClientBuilder.build().post()
                .uri("http://PAYMENT-SERVICE/payment/create-payment-intent")
                .bodyValue(request).retrieve().bodyToMono(Response.class).block();
    }
}
