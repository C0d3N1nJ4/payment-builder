package com.naiomi.payment.builder.service;

import com.naiomi.payment.builder.model.PaymentRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserServiceTest {

    private CsvParserService csvParserService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        csvParserService = new CsvParserService();
    }

    @Test
    void testParseCsvFile_ValidFile() throws IOException {
        // Given
        String csvContent = """
                debtor_name,debtor_iban,creditor_name,creditor_iban,amount,currency,execution_date,end_to_end_id,remittance_info
                John Doe,DE89370400440532013000,Jane Smith,GB29NWBK60161331926819,1000.50,EUR,2025-11-15,INV-12345,Payment for Invoice 12345
                Acme Corp,FR1420041010050500013M02606,Tech Solutions,NL91ABNA0417164300,5500.00,USD,2025-11-16,PO-98765,Purchase Order 98765
                """;
        
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        // When
        List<PaymentRecord> records = csvParserService.parseCsvFile(csvFile);

        // Then
        assertEquals(2, records.size());
        
        PaymentRecord record1 = records.get(0);
        assertEquals("John Doe", record1.getDebtorName());
        assertEquals("DE89370400440532013000", record1.getDebtorAccountIBAN());
        assertEquals("Jane Smith", record1.getCreditorName());
        assertEquals("GB29NWBK60161331926819", record1.getCreditorAccountIBAN());
        assertEquals(new BigDecimal("1000.50"), record1.getInstructedAmount());
        assertEquals("EUR", record1.getCurrency());
        assertEquals(LocalDate.of(2025, 11, 15), record1.getRequestedExecutionDate());
        assertEquals("INV-12345", record1.getEndToEndId());
        assertEquals("Payment for Invoice 12345", record1.getRemittanceInformationUnstructured());
        
        PaymentRecord record2 = records.get(1);
        assertEquals("Acme Corp", record2.getDebtorName());
        assertEquals(new BigDecimal("5500.00"), record2.getInstructedAmount());
        assertEquals("USD", record2.getCurrency());
    }

    @Test
    void testParseCsvFile_AlternativeHeaderNames() throws IOException {
        // Given
        String csvContent = """
                payer_name,payer_iban,payee_name,payee_iban,payment_amount,ccy,payment_date,reference,payment_reference
                John Doe,DE89370400440532013000,Jane Smith,GB29NWBK60161331926819,1000.50,EUR,2025-11-15,INV-12345,Payment for Invoice 12345
                """;
        
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        // When
        List<PaymentRecord> records = csvParserService.parseCsvFile(csvFile);

        // Then
        assertEquals(1, records.size());
        
        PaymentRecord record = records.get(0);
        assertEquals("John Doe", record.getDebtorName());
        assertEquals("Jane Smith", record.getCreditorName());
        assertEquals(new BigDecimal("1000.50"), record.getInstructedAmount());
    }

    @Test
    void testParseCsvFile_WithOptionalFields() throws IOException {
        // Given
        String csvContent = """
                debtor_name,debtor_iban,debtor_bic,debtor_country,creditor_name,creditor_iban,creditor_bic,creditor_country,amount,currency,charge_bearer,purpose_code
                John Doe,DE89370400440532013000,COBADEFFXXX,DE,Jane Smith,GB29NWBK60161331926819,NWBKGB2LXXX,GB,1000.50,EUR,SLEV,SALA
                """;
        
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        // When
        List<PaymentRecord> records = csvParserService.parseCsvFile(csvFile);

        // Then
        assertEquals(1, records.size());
        
        PaymentRecord record = records.get(0);
        assertEquals("COBADEFFXXX", record.getDebtorBIC());
        assertEquals("DE", record.getDebtorCountry());
        assertEquals("NWBKGB2LXXX", record.getCreditorBIC());
        assertEquals("GB", record.getCreditorCountry());
        assertEquals("SLEV", record.getChargeBearer());
        assertEquals("SALA", record.getPurposeCode());
    }

    @Test
    void testParseCsvFile_EmptyFile() throws IOException {
        // Given
        Path csvFile = tempDir.resolve("empty.csv");
        Files.writeString(csvFile, "");

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            csvParserService.parseCsvFile(csvFile);
        });
    }

    @Test
    void testParseCsvFile_OnlyHeader() throws IOException {
        // Given
        String csvContent = "debtor_name,creditor_name,amount,currency\n";
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        // When
        List<PaymentRecord> records = csvParserService.parseCsvFile(csvFile);

        // Then
        assertEquals(0, records.size());
    }

    @Test
    void testParseCsvFile_InvalidAmount() throws IOException {
        // Given
        String csvContent = """
                debtor_name,creditor_name,amount,currency
                John Doe,Jane Smith,invalid_amount,EUR
                """;
        
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            csvParserService.parseCsvFile(csvFile);
        });
        assertTrue(exception.getMessage().contains("Error parsing line"));
    }

    @Test
    void testParseCsvFile_InvalidDate() throws IOException {
        // Given
        String csvContent = """
                debtor_name,creditor_name,amount,currency,execution_date
                John Doe,Jane Smith,1000.50,EUR,invalid-date
                """;
        
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            csvParserService.parseCsvFile(csvFile);
        });
        assertTrue(exception.getMessage().contains("Error parsing line"));
    }

    @Test
    void testParseCsvFile_WithEmptyLines() throws IOException {
        // Given
        String csvContent = """
                debtor_name,creditor_name,amount,currency
                John Doe,Jane Smith,1000.50,EUR
                
                Acme Corp,Tech Solutions,2000.00,USD
                """;
        
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        // When
        List<PaymentRecord> records = csvParserService.parseCsvFile(csvFile);

        // Then
        assertEquals(2, records.size());
    }

    @Test
    void testParseCsvFile_WithMissingOptionalFields() throws IOException {
        // Given
        String csvContent = """
                debtor_name,debtor_iban,creditor_name,creditor_iban,amount,currency
                John Doe,DE89370400440532013000,Jane Smith,GB29NWBK60161331926819,1000.50,EUR
                """;
        
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        // When
        List<PaymentRecord> records = csvParserService.parseCsvFile(csvFile);

        // Then
        assertEquals(1, records.size());
        
        PaymentRecord record = records.get(0);
        assertNull(record.getDebtorBIC());
        assertNull(record.getRequestedExecutionDate());
        assertNull(record.getEndToEndId());
        assertNull(record.getRemittanceInformationUnstructured());
    }
}
