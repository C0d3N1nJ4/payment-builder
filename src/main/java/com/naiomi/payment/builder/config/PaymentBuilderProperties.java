package com.naiomi.payment.builder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payment.builder")
public class PaymentBuilderProperties {

    private final Input input = new Input();
    private final Output output = new Output();

    public Input getInput() {
        return input;
    }

    public Output getOutput() {
        return output;
    }

    public static class Input {
        private String directory;

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }
    }

    public static class Output {
        private String directory;

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }
    }
}
