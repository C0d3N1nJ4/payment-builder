package com.naiomi.payment.builder.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

/**
 * Represents a payment record from CSV input
 */
@Data
public class PaymentRecord {
    
    // Debtor (Payer) Information
    private String debtorName;
    private String debtorAccountIBAN;
    private String debtorAccountOther;
    private String debtorBIC;
    private String debtorAddressLine1;
    private String debtorAddressLine2;
    private String debtorCountry;
    
    // Creditor (Payee) Information
    private String creditorName;
    private String creditorAccountIBAN;
    private String creditorAccountOther;
    private String creditorBIC;
    private String creditorAddressLine1;
    private String creditorAddressLine2;
    private String creditorCountry;
    
    // Payment Information
    private BigDecimal instructedAmount;
    private String currency;
    private LocalDate requestedExecutionDate;
    private String endToEndId;
    private String instructionId;
    
    // Remittance Information
    private String remittanceInformationUnstructured;
    private String remittanceInformationStructured;
    
    // Additional Information
    private String purposeCode;
    private String categoryPurposeCode;
    private String chargeBearer;
}
