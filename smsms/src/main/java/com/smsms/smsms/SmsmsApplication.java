package com.smsms.smsms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SmsmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmsmsApplication.class, args);
	}

}
