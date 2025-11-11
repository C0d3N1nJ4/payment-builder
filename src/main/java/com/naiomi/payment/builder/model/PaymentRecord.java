package com.naiomi.payment.builder.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a payment record from CSV input
 */
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
    
    // Getters and Setters
    
    public String getDebtorName() {
        return debtorName;
    }
    
    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }
    
    public String getDebtorAccountIBAN() {
        return debtorAccountIBAN;
    }
    
    public void setDebtorAccountIBAN(String debtorAccountIBAN) {
        this.debtorAccountIBAN = debtorAccountIBAN;
    }
    
    public String getDebtorAccountOther() {
        return debtorAccountOther;
    }
    
    public void setDebtorAccountOther(String debtorAccountOther) {
        this.debtorAccountOther = debtorAccountOther;
    }
    
    public String getDebtorBIC() {
        return debtorBIC;
    }
    
    public void setDebtorBIC(String debtorBIC) {
        this.debtorBIC = debtorBIC;
    }
    
    public String getDebtorAddressLine1() {
        return debtorAddressLine1;
    }
    
    public void setDebtorAddressLine1(String debtorAddressLine1) {
        this.debtorAddressLine1 = debtorAddressLine1;
    }
    
    public String getDebtorAddressLine2() {
        return debtorAddressLine2;
    }
    
    public void setDebtorAddressLine2(String debtorAddressLine2) {
        this.debtorAddressLine2 = debtorAddressLine2;
    }
    
    public String getDebtorCountry() {
        return debtorCountry;
    }
    
    public void setDebtorCountry(String debtorCountry) {
        this.debtorCountry = debtorCountry;
    }
    
    public String getCreditorName() {
        return creditorName;
    }
    
    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }
    
    public String getCreditorAccountIBAN() {
        return creditorAccountIBAN;
    }
    
    public void setCreditorAccountIBAN(String creditorAccountIBAN) {
        this.creditorAccountIBAN = creditorAccountIBAN;
    }
    
    public String getCreditorAccountOther() {
        return creditorAccountOther;
    }
    
    public void setCreditorAccountOther(String creditorAccountOther) {
        this.creditorAccountOther = creditorAccountOther;
    }
    
    public String getCreditorBIC() {
        return creditorBIC;
    }
    
    public void setCreditorBIC(String creditorBIC) {
        this.creditorBIC = creditorBIC;
    }
    
    public String getCreditorAddressLine1() {
        return creditorAddressLine1;
    }
    
    public void setCreditorAddressLine1(String creditorAddressLine1) {
        this.creditorAddressLine1 = creditorAddressLine1;
    }
    
    public String getCreditorAddressLine2() {
        return creditorAddressLine2;
    }
    
    public void setCreditorAddressLine2(String creditorAddressLine2) {
        this.creditorAddressLine2 = creditorAddressLine2;
    }
    
    public String getCreditorCountry() {
        return creditorCountry;
    }
    
    public void setCreditorCountry(String creditorCountry) {
        this.creditorCountry = creditorCountry;
    }
    
    public BigDecimal getInstructedAmount() {
        return instructedAmount;
    }
    
    public void setInstructedAmount(BigDecimal instructedAmount) {
        this.instructedAmount = instructedAmount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public LocalDate getRequestedExecutionDate() {
        return requestedExecutionDate;
    }
    
    public void setRequestedExecutionDate(LocalDate requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }
    
    public String getEndToEndId() {
        return endToEndId;
    }
    
    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }
    
    public String getInstructionId() {
        return instructionId;
    }
    
    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId;
    }
    
    public String getRemittanceInformationUnstructured() {
        return remittanceInformationUnstructured;
    }
    
    public void setRemittanceInformationUnstructured(String remittanceInformationUnstructured) {
        this.remittanceInformationUnstructured = remittanceInformationUnstructured;
    }
    
    public String getRemittanceInformationStructured() {
        return remittanceInformationStructured;
    }
    
    public void setRemittanceInformationStructured(String remittanceInformationStructured) {
        this.remittanceInformationStructured = remittanceInformationStructured;
    }
    
    public String getPurposeCode() {
        return purposeCode;
    }
    
    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }
    
    public String getCategoryPurposeCode() {
        return categoryPurposeCode;
    }
    
    public void setCategoryPurposeCode(String categoryPurposeCode) {
        this.categoryPurposeCode = categoryPurposeCode;
    }
    
    public String getChargeBearer() {
        return chargeBearer;
    }
    
    public void setChargeBearer(String chargeBearer) {
        this.chargeBearer = chargeBearer;
    }
}
