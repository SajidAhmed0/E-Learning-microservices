package com.paymentms.paymentms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PaymentmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentmsApplication.class, args);
	}

}
