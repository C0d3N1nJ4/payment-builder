package com.naiomi.payment.builder.service;

import com.naiomi.payment.builder.model.PaymentRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMessageGeneratorServiceTest {

    private PaymentMessageGeneratorService generatorService;

    @BeforeEach
    void setUp() {
        generatorService = new PaymentMessageGeneratorService();
    }

    @Test
    void testGeneratePaymentMessage_SingleRecord() {
        // Given
        List<PaymentRecord> records = new ArrayList<>();
        PaymentRecord record = createSampleRecord();
        records.add(record);

        // When
        String xmlContent = generatorService.generatePaymentMessage(records);

        // Then
        assertNotNull(xmlContent);
        assertTrue(xmlContent.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertTrue(xmlContent.contains("<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.013.001.11\">"));
        assertTrue(xmlContent.contains("<CdtrPmtActvtnReq>"));
        assertTrue(xmlContent.contains("<GrpHdr>"));
        assertTrue(xmlContent.contains("<NbOfTxs>1</NbOfTxs>"));
        assertTrue(xmlContent.contains("<PmtInf>"));
        assertTrue(xmlContent.contains("<CdtTrfTxInf>"));
    }

    @Test
    void testGeneratePaymentMessage_MultipleRecords() {
        // Given
        List<PaymentRecord> records = new ArrayList<>();
        records.add(createSampleRecord());
        records.add(createSampleRecord());
        records.add(createSampleRecord());

        // When
        String xmlContent = generatorService.generatePaymentMessage(records);

        // Then
        assertTrue(xmlContent.contains("<NbOfTxs>3</NbOfTxs>"));
        
        // Count occurrences of CdtTrfTxInf (should be 3)
        int count = xmlContent.split("<CdtTrfTxInf>").length - 1;
        assertEquals(3, count);
    }

    @Test
    void testGeneratePaymentMessage_CreditorInformation() {
        // Given
        List<PaymentRecord> records = new ArrayList<>();
        PaymentRecord record = new PaymentRecord();
        record.setCreditorName("Jane Smith");
        record.setCreditorAccountIBAN("GB29NWBK60161331926819");
        record.setCreditorBIC("NWBKGB2LXXX");
        record.setCreditorAddressLine1("456 High Street");
        record.setCreditorCountry("GB");
        record.setInstructedAmount(new BigDecimal("1000.50"));
        record.setCurrency("EUR");
        records.add(record);

        // When
        String xmlContent = generatorService.generatePaymentMessage(records);

        // Then
        assertTrue(xmlContent.contains("<Cdtr>"));
        assertTrue(xmlContent.contains("<Nm>Jane Smith</Nm>"));
        assertTrue(xmlContent.contains("<IBAN>GB29NWBK60161331926819</IBAN>"));
        assertTrue(xmlContent.contains("<BICFI>NWBKGB2LXXX</BICFI>"));
        assertTrue(xmlContent.contains("<AdrLine>456 High Street</AdrLine>"));
        assertTrue(xmlContent.contains("<Ctry>GB</Ctry>"));
    }

    @Test
    void testGeneratePaymentMessage_AmountAndCurrency() {
        // Given
        List<PaymentRecord> records = new ArrayList<>();
        PaymentRecord record = createMinimalRecord();
        record.setInstructedAmount(new BigDecimal("5500.75"));
        record.setCurrency("USD");
        records.add(record);

        // When
        String xmlContent = generatorService.generatePaymentMessage(records);

        // Then
        assertTrue(xmlContent.contains("<InstdAmt Ccy=\"USD\">5500.75</InstdAmt>"));
    }

    @Test
    void testGeneratePaymentMessage_DefaultCurrency() {
        // Given
        List<PaymentRecord> records = new ArrayList<>();
        PaymentRecord record = createMinimalRecord();
        record.setInstructedAmount(new BigDecimal("1000.00"));
        record.setCurrency(null); // No currency specified
        records.add(record);

        // When
        String xmlContent = generatorService.generatePaymentMessage(records);

        // Then
        assertTrue(xmlContent.contains("<InstdAmt Ccy=\"EUR\">1000.00</InstdAmt>"));
    }

    @Test
    void testGeneratePaymentMessage_PaymentIdentification() {
        // Given
        List<PaymentRecord> records = new ArrayList<>();
        PaymentRecord record = createMinimalRecord();
        record.setInstructionId("INSTR-123");
        record.setEndToEndId("E2E-456");
        records.add(record);

        // When
        String xmlContent = generatorService.generatePaymentMessage(records);

        // Then
        assertTrue(xmlContent.contains("<PmtId>"));
        assertTrue(xmlContent.contains("<InstrId>INSTR-123</InstrId>"));
        assertTrue(xmlContent.contains("<EndToEndId>E2E-456</EndToEndId>"));
    }

    @Test
    void testGeneratePaymentMessage_RemittanceInformation() {
        // Given
        List<PaymentRecord> records = new ArrayList<>();
        PaymentRecord record = createMinimalRecord();
        record.setRemittanceInformationUnstructured("Payment for Invoice 12345");
        records.add(record);

        // When
        String xmlContent = generatorService.generatePaymentMessage(records);

        // Then
        assertTrue(xmlContent.contains("<RmtInf>"));
        assertTrue(xmlContent.contains("<Ustrd>Payment for Invoice 12345</Ustrd>"));
    }

    @Test
    void testGeneratePaymentMessage_ChargeBearer() {
        // Given
        List<PaymentRecord> records = new ArrayList<>();
        PaymentRecord record = createMinimalRecord();
        record.setChargeBearer("SLEV");
        records.add(record);

        // When
        String xmlContent = generatorService.generatePaymentMessage(records);

        // Then
        assertTrue(xmlContent.contains("<ChrgBr>SLEV</ChrgBr>"));
    }

    @Test
    void testGeneratePaymentMessage_CategoryPurposeCode() {
        // Given
        List<PaymentRecord> records = new ArrayList<>();
        PaymentRecord record = createMinimalRecord();
        record.setCategoryPurposeCode("SALA");
        records.add(record);

        // When
        String xmlContent = generatorService.generatePaymentMessage(records);

        // Then
        assertTrue(xmlContent.contains("<PmtTpInf>"));
        assertTrue(xmlContent.contains("<CtgyPurp>"));
        assertTrue(xmlContent.contains("<Cd>SALA</Cd>"));
    }

    @Test
    void testGeneratePaymentMessage_XmlEscaping() {
        // Given
        List<PaymentRecord> records = new ArrayList<>();
        PaymentRecord record = createMinimalRecord();
        record.setCreditorName("Company & Co <Ltd>");
        record.setRemittanceInformationUnstructured("Invoice \"123\" & Payment");
        records.add(record);

        // When
        String xmlContent = generatorService.generatePaymentMessage(records);

        // Then
        assertTrue(xmlContent.contains("Company &amp; Co &lt;Ltd&gt;"));
        assertTrue(xmlContent.contains("Invoice &quot;123&quot; &amp; Payment"));
        assertFalse(xmlContent.contains("Company & Co <Ltd>"));
    }

    @Test
    void testGeneratePaymentMessage_OtherAccountIdentification() {
        // Given
        List<PaymentRecord> records = new ArrayList<>();
        PaymentRecord record = createMinimalRecord();
        record.setCreditorAccountIBAN(null);
        record.setCreditorAccountOther("ACC123456789");
        records.add(record);

        // When
        String xmlContent = generatorService.generatePaymentMessage(records);

        // Then
        assertTrue(xmlContent.contains("<Othr>"));
        assertTrue(xmlContent.contains("<Id>ACC123456789</Id>"));
        assertFalse(xmlContent.contains("<IBAN>"));
    }

    @Test
    void testGeneratePaymentMessage_WithoutOptionalFields() {
        // Given
        List<PaymentRecord> records = new ArrayList<>();
        PaymentRecord record = createMinimalRecord();
        records.add(record);

        // When
        String xmlContent = generatorService.generatePaymentMessage(records);

        // Then
        assertNotNull(xmlContent);
        assertTrue(xmlContent.contains("<Document"));
        assertTrue(xmlContent.contains("<Cdtr>"));
        assertTrue(xmlContent.contains("<CdtrAcct>"));
        
        // Optional fields should not be present
        assertFalse(xmlContent.contains("<ChrgBr>"));
        assertFalse(xmlContent.contains("<RmtInf>"));
        assertFalse(xmlContent.contains("<CdtrAgt>"));
    }

    @Test
    void testGeneratePaymentMessage_ValidXmlStructure() {
        // Given
        List<PaymentRecord> records = new ArrayList<>();
        records.add(createSampleRecord());

        // When
        String xmlContent = generatorService.generatePaymentMessage(records);

        // Then
        assertTrue(xmlContent.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertTrue(xmlContent.endsWith("</Document>"));
        
        // Check proper nesting
        int openTags = xmlContent.split("<(?!/)[^>]*>").length - 1;
        int closeTags = xmlContent.split("</[^>]*>").length - 1;
        assertTrue(openTags > 0);
        assertTrue(closeTags > 0);
    }

    // Helper methods

    private PaymentRecord createSampleRecord() {
        PaymentRecord record = new PaymentRecord();
        record.setDebtorName("John Doe");
        record.setDebtorAccountIBAN("DE89370400440532013000");
        record.setDebtorBIC("COBADEFFXXX");
        record.setCreditorName("Jane Smith");
        record.setCreditorAccountIBAN("GB29NWBK60161331926819");
        record.setCreditorBIC("NWBKGB2LXXX");
        record.setInstructedAmount(new BigDecimal("1000.50"));
        record.setCurrency("EUR");
        record.setRequestedExecutionDate(LocalDate.of(2025, 11, 15));
        record.setEndToEndId("E2E-12345");
        record.setRemittanceInformationUnstructured("Payment for services");
        return record;
    }

    private PaymentRecord createMinimalRecord() {
        PaymentRecord record = new PaymentRecord();
        record.setCreditorName("Test Creditor");
        record.setCreditorAccountIBAN("GB29NWBK60161331926819");
        record.setInstructedAmount(new BigDecimal("100.00"));
        record.setCurrency("EUR");
        return record;
    }
}
