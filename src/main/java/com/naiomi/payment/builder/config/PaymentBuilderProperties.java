package com.naiomi.payment.builder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Configuration
@ConfigurationProperties(prefix = "payment.builder")
@Getter
@RequiredArgsConstructor
public class PaymentBuilderProperties {

    private final Input input;
    private final Output output;

    @Data
    public static class Input {
        private String directory;
    }

    @Data
    public static class Output {
        private String directory;
    }
}
