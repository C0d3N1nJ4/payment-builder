package com.naiomi.payment.builder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PaymentBuilderApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentBuilderApplication.class, args);
	}

}
