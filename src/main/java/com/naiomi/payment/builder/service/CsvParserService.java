package com.naiomi.payment.builder.service;

import com.naiomi.payment.builder.model.PaymentRecord;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for parsing CSV files and mapping fields to PaymentRecord objects
 */
@Service
public class CsvParserService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Parse CSV file and return list of payment records
     * 
     * @param csvFilePath Path to the CSV file
     * @return List of PaymentRecord objects
     * @throws IOException if file reading fails
     */
    public List<PaymentRecord> parseCsvFile(Path csvFilePath) throws IOException {
        List<PaymentRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath.toFile()))) {
            // Read header line
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("CSV file is empty");
            }
            
            String[] headers = headerLine.split(",");
            Map<String, Integer> headerMap = createHeaderMap(headers);
            
            // Read data lines
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                
                try {
                    PaymentRecord record = parseLine(line, headerMap);
                    records.add(record);
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing line " + lineNumber + ": " + e.getMessage(), e);
                }
            }
        }
        
        return records;
    }
    
    /**
     * Create a map of header names to column indices
     */
    private Map<String, Integer> createHeaderMap(String[] headers) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            map.put(headers[i].trim().toLowerCase(), i);
        }
        return map;
    }
    
    /**
     * Parse a single CSV line into a PaymentRecord
     */
    private PaymentRecord parseLine(String line, Map<String, Integer> headerMap) {
        String[] values = line.split(",", -1); // -1 to include trailing empty strings
        PaymentRecord record = new PaymentRecord();
        
        // Map debtor fields
        record.setDebtorName(getStringValue(values, headerMap, "debtor_name", "debtorname", "payer_name"));
        record.setDebtorAccountIBAN(getStringValue(values, headerMap, "debtor_iban", "debtor_account_iban", "payer_iban"));
        record.setDebtorAccountOther(getStringValue(values, headerMap, "debtor_account_other", "debtor_account"));
        record.setDebtorBIC(getStringValue(values, headerMap, "debtor_bic", "payer_bic"));
        record.setDebtorAddressLine1(getStringValue(values, headerMap, "debtor_address_line1", "debtor_address1"));
        record.setDebtorAddressLine2(getStringValue(values, headerMap, "debtor_address_line2", "debtor_address2"));
        record.setDebtorCountry(getStringValue(values, headerMap, "debtor_country", "payer_country"));
        
        // Map creditor fields
        record.setCreditorName(getStringValue(values, headerMap, "creditor_name", "creditorname", "payee_name"));
        record.setCreditorAccountIBAN(getStringValue(values, headerMap, "creditor_iban", "creditor_account_iban", "payee_iban"));
        record.setCreditorAccountOther(getStringValue(values, headerMap, "creditor_account_other", "creditor_account"));
        record.setCreditorBIC(getStringValue(values, headerMap, "creditor_bic", "payee_bic"));
        record.setCreditorAddressLine1(getStringValue(values, headerMap, "creditor_address_line1", "creditor_address1"));
        record.setCreditorAddressLine2(getStringValue(values, headerMap, "creditor_address_line2", "creditor_address2"));
        record.setCreditorCountry(getStringValue(values, headerMap, "creditor_country", "payee_country"));
        
        // Map payment fields
        record.setInstructedAmount(getBigDecimalValue(values, headerMap, "amount", "instructed_amount", "payment_amount"));
        record.setCurrency(getStringValue(values, headerMap, "currency", "ccy"));
        record.setRequestedExecutionDate(getDateValue(values, headerMap, "execution_date", "requested_execution_date", "payment_date"));
        record.setEndToEndId(getStringValue(values, headerMap, "end_to_end_id", "endtoendid", "reference"));
        record.setInstructionId(getStringValue(values, headerMap, "instruction_id", "instructionid"));
        
        // Map remittance information
        record.setRemittanceInformationUnstructured(getStringValue(values, headerMap, "remittance_info", "remittance_information", "payment_reference"));
        record.setRemittanceInformationStructured(getStringValue(values, headerMap, "remittance_structured", "structured_remittance"));
        
        // Map additional fields
        record.setPurposeCode(getStringValue(values, headerMap, "purpose_code", "purpose"));
        record.setCategoryPurposeCode(getStringValue(values, headerMap, "category_purpose_code", "category_purpose"));
        record.setChargeBearer(getStringValue(values, headerMap, "charge_bearer", "charges"));
        
        return record;
    }
    
    /**
     * Get string value from values array, trying multiple possible header names
     */
    private String getStringValue(String[] values, Map<String, Integer> headerMap, String... possibleHeaders) {
        for (String header : possibleHeaders) {
            Integer index = headerMap.get(header.toLowerCase());
            if (index != null && index < values.length) {
                String value = values[index].trim();
                if (!value.isEmpty()) {
                    return value;
                }
            }
        }
        return null;
    }
    
    /**
     * Get BigDecimal value from values array
     */
    private BigDecimal getBigDecimalValue(String[] values, Map<String, Integer> headerMap, String... possibleHeaders) {
        String stringValue = getStringValue(values, headerMap, possibleHeaders);
        if (stringValue != null && !stringValue.isEmpty()) {
            try {
                return new BigDecimal(stringValue);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid decimal value: " + stringValue);
            }
        }
        return null;
    }
    
    /**
     * Get LocalDate value from values array
     */
    private LocalDate getDateValue(String[] values, Map<String, Integer> headerMap, String... possibleHeaders) {
        String stringValue = getStringValue(values, headerMap, possibleHeaders);
        if (stringValue != null && !stringValue.isEmpty()) {
            try {
                return LocalDate.parse(stringValue, DATE_FORMATTER);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date value: " + stringValue + ". Expected format: yyyy-MM-dd");
            }
        }
        return null;
    }
}
