package com.naiomi.payment.builder.service;

import com.naiomi.payment.builder.config.PaymentBuilderProperties;
import com.naiomi.payment.builder.model.PaymentRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentBuilderServiceTest {

    @Mock
    private PaymentBuilderProperties properties;

    @Mock
    private PaymentBuilderProperties.Input input;

    @Mock
    private PaymentBuilderProperties.Output output;

    @Mock
    private CsvParserService csvParserService;

    @Mock
    private PaymentMessageGeneratorService messageGeneratorService;

    private PaymentBuilderService paymentBuilderService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        when(properties.getInput()).thenReturn(input);
        when(properties.getOutput()).thenReturn(output);
        
        paymentBuilderService = new PaymentBuilderService(
                properties, 
                csvParserService, 
                messageGeneratorService
        );
    }

    @Test
    void testProcessInputFiles_SingleFile() throws IOException {
        // Given
        Path inputDir = tempDir.resolve("input");
        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(inputDir);
        
        Path csvFile = inputDir.resolve("payments.csv");
        Files.writeString(csvFile, "test,data\n");
        
        when(input.getDirectory()).thenReturn(inputDir.toString());
        when(output.getDirectory()).thenReturn(outputDir.toString());
        
        List<PaymentRecord> records = createSampleRecords();
        when(csvParserService.parseCsvFile(any())).thenReturn(records);
        when(messageGeneratorService.generatePaymentMessage(any())).thenReturn("<xml>test</xml>");

        // When
        int processedCount = paymentBuilderService.processInputFiles();

        // Then
        assertEquals(1, processedCount);
        verify(csvParserService, times(1)).parseCsvFile(any());
        verify(messageGeneratorService, times(1)).generatePaymentMessage(any());
        
        // Verify output file was created
        Path outputFile = outputDir.resolve("payments_pain013.xml");
        assertTrue(Files.exists(outputFile));
        assertEquals("<xml>test</xml>", Files.readString(outputFile));
    }

    @Test
    void testProcessInputFiles_MultipleFiles() throws IOException {
        // Given
        Path inputDir = tempDir.resolve("input");
        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(inputDir);
        
        Path csvFile1 = inputDir.resolve("payments1.csv");
        Path csvFile2 = inputDir.resolve("payments2.csv");
        Path csvFile3 = inputDir.resolve("payments3.csv");
        Files.writeString(csvFile1, "test,data\n");
        Files.writeString(csvFile2, "test,data\n");
        Files.writeString(csvFile3, "test,data\n");
        
        when(input.getDirectory()).thenReturn(inputDir.toString());
        when(output.getDirectory()).thenReturn(outputDir.toString());
        
        List<PaymentRecord> records = createSampleRecords();
        when(csvParserService.parseCsvFile(any())).thenReturn(records);
        when(messageGeneratorService.generatePaymentMessage(any())).thenReturn("<xml>test</xml>");

        // When
        int processedCount = paymentBuilderService.processInputFiles();

        // Then
        assertEquals(3, processedCount);
        verify(csvParserService, times(3)).parseCsvFile(any());
        verify(messageGeneratorService, times(3)).generatePaymentMessage(any());
    }

    @Test
    void testProcessInputFiles_NoFiles() throws IOException {
        // Given
        Path inputDir = tempDir.resolve("input");
        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(inputDir);
        
        when(input.getDirectory()).thenReturn(inputDir.toString());
        when(output.getDirectory()).thenReturn(outputDir.toString());

        // When
        int processedCount = paymentBuilderService.processInputFiles();

        // Then
        assertEquals(0, processedCount);
        verify(csvParserService, never()).parseCsvFile(any());
        verify(messageGeneratorService, never()).generatePaymentMessage(any());
    }

    @Test
    void testProcessInputFiles_OnlyNonCsvFiles() throws IOException {
        // Given
        Path inputDir = tempDir.resolve("input");
        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(inputDir);
        
        Path txtFile = inputDir.resolve("data.txt");
        Path xmlFile = inputDir.resolve("config.xml");
        Files.writeString(txtFile, "test");
        Files.writeString(xmlFile, "<xml>test</xml>");
        
        when(input.getDirectory()).thenReturn(inputDir.toString());
        when(output.getDirectory()).thenReturn(outputDir.toString());

        // When
        int processedCount = paymentBuilderService.processInputFiles();

        // Then
        assertEquals(0, processedCount);
        verify(csvParserService, never()).parseCsvFile(any());
    }

    @Test
    void testProcessInputFiles_CreatesDirectories() throws IOException {
        // Given
        Path inputDir = tempDir.resolve("new_input");
        Path outputDir = tempDir.resolve("new_output");
        
        when(input.getDirectory()).thenReturn(inputDir.toString());
        when(output.getDirectory()).thenReturn(outputDir.toString());

        // When
        paymentBuilderService.processInputFiles();

        // Then
        assertTrue(Files.exists(inputDir));
        assertTrue(Files.exists(outputDir));
        assertTrue(Files.isDirectory(inputDir));
        assertTrue(Files.isDirectory(outputDir));
    }

    @Test
    void testProcessInputFiles_ContinuesOnError() throws IOException {
        // Given
        Path inputDir = tempDir.resolve("input");
        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(inputDir);
        
        Path csvFile1 = inputDir.resolve("good.csv");
        Path csvFile2 = inputDir.resolve("bad.csv");
        Path csvFile3 = inputDir.resolve("good2.csv");
        Files.writeString(csvFile1, "test,data\n");
        Files.writeString(csvFile2, "test,data\n");
        Files.writeString(csvFile3, "test,data\n");
        
        when(input.getDirectory()).thenReturn(inputDir.toString());
        when(output.getDirectory()).thenReturn(outputDir.toString());
        
        List<PaymentRecord> records = createSampleRecords();
        
        // Mock to throw exception for bad.csv
        when(csvParserService.parseCsvFile(any())).thenAnswer(invocation -> {
            Path path = invocation.getArgument(0);
            if (path.getFileName().toString().equals("bad.csv")) {
                throw new RuntimeException("Invalid CSV format");
            }
            return records;
        });
        
        when(messageGeneratorService.generatePaymentMessage(any())).thenReturn("<xml>test</xml>");

        // When
        int processedCount = paymentBuilderService.processInputFiles();

        // Then
        assertEquals(2, processedCount); // Only good.csv and good2.csv processed
        verify(csvParserService, times(3)).parseCsvFile(any());
        verify(messageGeneratorService, times(2)).generatePaymentMessage(any());
    }

    @Test
    void testProcessFile_Success() throws IOException {
        // Given
        Path csvFile = tempDir.resolve("test.csv");
        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(outputDir);
        Files.writeString(csvFile, "test,data\n");
        
        List<PaymentRecord> records = createSampleRecords();
        when(csvParserService.parseCsvFile(csvFile)).thenReturn(records);
        when(messageGeneratorService.generatePaymentMessage(records)).thenReturn("<xml>payment</xml>");

        // When
        paymentBuilderService.processFile(csvFile, outputDir);

        // Then
        Path outputFile = outputDir.resolve("test_pain013.xml");
        assertTrue(Files.exists(outputFile));
        assertEquals("<xml>payment</xml>", Files.readString(outputFile));
    }

    @Test
    void testProcessFile_EmptyRecords() throws IOException {
        // Given
        Path csvFile = tempDir.resolve("empty.csv");
        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(outputDir);
        Files.writeString(csvFile, "header\n");
        
        when(csvParserService.parseCsvFile(csvFile)).thenReturn(new ArrayList<>());

        // When
        paymentBuilderService.processFile(csvFile, outputDir);

        // Then
        verify(messageGeneratorService, never()).generatePaymentMessage(any());
        
        Path outputFile = outputDir.resolve("empty_pain013.xml");
        assertFalse(Files.exists(outputFile));
    }

    @Test
    void testProcessFile_GeneratesCorrectOutputFileName() throws IOException {
        // Given
        Path csvFile = tempDir.resolve("monthly_payments_2025.csv");
        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(outputDir);
        Files.writeString(csvFile, "test,data\n");
        
        List<PaymentRecord> records = createSampleRecords();
        when(csvParserService.parseCsvFile(csvFile)).thenReturn(records);
        when(messageGeneratorService.generatePaymentMessage(records)).thenReturn("<xml>test</xml>");

        // When
        paymentBuilderService.processFile(csvFile, outputDir);

        // Then
        Path outputFile = outputDir.resolve("monthly_payments_2025_pain013.xml");
        assertTrue(Files.exists(outputFile));
    }

    // Helper method
    private List<PaymentRecord> createSampleRecords() {
        List<PaymentRecord> records = new ArrayList<>();
        PaymentRecord record = new PaymentRecord();
        record.setCreditorName("Test Creditor");
        record.setCreditorAccountIBAN("GB29NWBK60161331926819");
        record.setInstructedAmount(new BigDecimal("1000.00"));
        record.setCurrency("EUR");
        records.add(record);
        return records;
    }
}
