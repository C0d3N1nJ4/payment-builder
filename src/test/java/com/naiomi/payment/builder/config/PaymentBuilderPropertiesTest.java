package com.naiomi.payment.builder.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "payment.builder.input.directory=/tmp/input",
        "payment.builder.output.directory=/tmp/output"
})
class PaymentBuilderPropertiesTest {

    @Autowired
    private PaymentBuilderProperties properties;

    @Test
    void testPropertiesLoaded() {
        assertNotNull(properties);
        assertNotNull(properties.getInput());
        assertNotNull(properties.getOutput());
    }

    @Test
    void testInputDirectoryProperty() {
        assertEquals("/tmp/input", properties.getInput().getDirectory());
    }

    @Test
    void testOutputDirectoryProperty() {
        assertEquals("/tmp/output", properties.getOutput().getDirectory());
    }

    @Test
    void testInputGetterSetter() {
        PaymentBuilderProperties.Input input = new PaymentBuilderProperties.Input();
        input.setDirectory("/custom/input");
        assertEquals("/custom/input", input.getDirectory());
    }

    @Test
    void testOutputGetterSetter() {
        PaymentBuilderProperties.Output output = new PaymentBuilderProperties.Output();
        output.setDirectory("/custom/output");
        assertEquals("/custom/output", output.getDirectory());
    }
}
