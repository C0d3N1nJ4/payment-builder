package com.naiomi.payment.builder.service;

import com.naiomi.payment.builder.model.PaymentRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service for generating ISO20022 pain.013 XML payment messages
 */
@Service
public class PaymentMessageGeneratorService {
    
    private static final String NAMESPACE = "urn:iso:std:iso:20022:tech:xsd:pain.013.001.11";
    private static final DateTimeFormatter XML_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter XML_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Generate ISO20022 pain.013 payment activation request message
     * 
     * @param records List of payment records
     * @return XML string
     */
    public String generatePaymentMessage(List<PaymentRecord> records) {
        StringBuilder xml = new StringBuilder();
        
        // XML Declaration
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        
        // Document root
        xml.append("<Document xmlns=\"").append(NAMESPACE).append("\">\n");
        xml.append("  <CdtrPmtActvtnReq>\n");
        
        // Group Header
        appendGroupHeader(xml, records.size());
        
        // Payment Information
        appendPaymentInformation(xml, records);
        
        xml.append("  </CdtrPmtActvtnReq>\n");
        xml.append("</Document>");
        
        return xml.toString();
    }
    
    /**
     * Append group header to XML
     */
    private void appendGroupHeader(StringBuilder xml, int numberOfTransactions) {
        String msgId = "MSG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String creationDateTime = LocalDateTime.now().format(XML_DATETIME_FORMATTER);
        
        xml.append("    <GrpHdr>\n");
        xml.append("      <MsgId>").append(msgId).append("</MsgId>\n");
        xml.append("      <CreDtTm>").append(creationDateTime).append("</CreDtTm>\n");
        xml.append("      <NbOfTxs>").append(numberOfTransactions).append("</NbOfTxs>\n");
        xml.append("      <InitgPty>\n");
        xml.append("        <Nm>Payment Builder System</Nm>\n");
        xml.append("      </InitgPty>\n");
        xml.append("    </GrpHdr>\n");
    }
    
    /**
     * Append payment information section
     */
    private void appendPaymentInformation(StringBuilder xml, List<PaymentRecord> records) {
        String pmtInfId = "PMTINF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        xml.append("    <PmtInf>\n");
        xml.append("      <PmtInfId>").append(pmtInfId).append("</PmtInfId>\n");
        xml.append("      <PmtMtd>TRF</PmtMtd>\n");
        
        // Credit Transfer Transaction Information for each record
        for (PaymentRecord record : records) {
            appendCreditTransferTransaction(xml, record);
        }
        
        xml.append("    </PmtInf>\n");
    }
    
    /**
     * Append credit transfer transaction information
     */
    private void appendCreditTransferTransaction(StringBuilder xml, PaymentRecord record) {
        xml.append("      <CdtTrfTxInf>\n");
        
        // Payment Identification
        appendPaymentIdentification(xml, record);
        
        // Payment Type Information
        if (record.getCategoryPurposeCode() != null || record.getPurposeCode() != null) {
            appendPaymentTypeInformation(xml, record);
        }
        
        // Amount
        appendAmount(xml, record);
        
        // Charge Bearer
        if (record.getChargeBearer() != null) {
            xml.append("        <ChrgBr>").append(record.getChargeBearer()).append("</ChrgBr>\n");
        }
        
        // Creditor Agent (Bank)
        if (record.getCreditorBIC() != null) {
            appendCreditorAgent(xml, record);
        }
        
        // Creditor (Payee)
        appendCreditor(xml, record);
        
        // Creditor Account
        appendCreditorAccount(xml, record);
        
        // Remittance Information
        if (record.getRemittanceInformationUnstructured() != null || 
            record.getRemittanceInformationStructured() != null) {
            appendRemittanceInformation(xml, record);
        }
        
        xml.append("      </CdtTrfTxInf>\n");
    }
    
    /**
     * Append payment identification
     */
    private void appendPaymentIdentification(StringBuilder xml, PaymentRecord record) {
        xml.append("        <PmtId>\n");
        
        if (record.getInstructionId() != null) {
            xml.append("          <InstrId>").append(escapeXml(record.getInstructionId())).append("</InstrId>\n");
        }
        
        String endToEndId = record.getEndToEndId() != null ? 
            record.getEndToEndId() : 
            "E2E-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        xml.append("          <EndToEndId>").append(escapeXml(endToEndId)).append("</EndToEndId>\n");
        
        xml.append("        </PmtId>\n");
    }
    
    /**
     * Append payment type information
     */
    private void appendPaymentTypeInformation(StringBuilder xml, PaymentRecord record) {
        xml.append("        <PmtTpInf>\n");
        
        if (record.getCategoryPurposeCode() != null) {
            xml.append("          <CtgyPurp>\n");
            xml.append("            <Cd>").append(record.getCategoryPurposeCode()).append("</Cd>\n");
            xml.append("          </CtgyPurp>\n");
        }
        
        xml.append("        </PmtTpInf>\n");
    }
    
    /**
     * Append amount information
     */
    private void appendAmount(StringBuilder xml, PaymentRecord record) {
        String currency = record.getCurrency() != null ? record.getCurrency() : "EUR";
        
        xml.append("        <Amt>\n");
        xml.append("          <InstdAmt Ccy=\"").append(currency).append("\">");
        xml.append(record.getInstructedAmount() != null ? record.getInstructedAmount().toString() : "0.00");
        xml.append("</InstdAmt>\n");
        xml.append("        </Amt>\n");
    }
    
    /**
     * Append creditor agent (bank) information
     */
    private void appendCreditorAgent(StringBuilder xml, PaymentRecord record) {
        xml.append("        <CdtrAgt>\n");
        xml.append("          <FinInstnId>\n");
        xml.append("            <BICFI>").append(record.getCreditorBIC()).append("</BICFI>\n");
        xml.append("          </FinInstnId>\n");
        xml.append("        </CdtrAgt>\n");
    }
    
    /**
     * Append creditor (payee) information
     */
    private void appendCreditor(StringBuilder xml, PaymentRecord record) {
        xml.append("        <Cdtr>\n");
        xml.append("          <Nm>").append(escapeXml(record.getCreditorName())).append("</Nm>\n");
        
        if (record.getCreditorAddressLine1() != null || record.getCreditorCountry() != null) {
            xml.append("          <PstlAdr>\n");
            
            if (record.getCreditorCountry() != null) {
                xml.append("            <Ctry>").append(record.getCreditorCountry()).append("</Ctry>\n");
            }
            
            if (record.getCreditorAddressLine1() != null) {
                xml.append("            <AdrLine>").append(escapeXml(record.getCreditorAddressLine1())).append("</AdrLine>\n");
            }
            
            if (record.getCreditorAddressLine2() != null) {
                xml.append("            <AdrLine>").append(escapeXml(record.getCreditorAddressLine2())).append("</AdrLine>\n");
            }
            
            xml.append("          </PstlAdr>\n");
        }
        
        xml.append("        </Cdtr>\n");
    }
    
    /**
     * Append creditor account information
     */
    private void appendCreditorAccount(StringBuilder xml, PaymentRecord record) {
        xml.append("        <CdtrAcct>\n");
        xml.append("          <Id>\n");
        
        if (record.getCreditorAccountIBAN() != null) {
            xml.append("            <IBAN>").append(record.getCreditorAccountIBAN()).append("</IBAN>\n");
        } else if (record.getCreditorAccountOther() != null) {
            xml.append("            <Othr>\n");
            xml.append("              <Id>").append(escapeXml(record.getCreditorAccountOther())).append("</Id>\n");
            xml.append("            </Othr>\n");
        }
        
        xml.append("          </Id>\n");
        xml.append("        </CdtrAcct>\n");
    }
    
    /**
     * Append remittance information
     */
    private void appendRemittanceInformation(StringBuilder xml, PaymentRecord record) {
        xml.append("        <RmtInf>\n");
        
        if (record.getRemittanceInformationUnstructured() != null) {
            xml.append("          <Ustrd>").append(escapeXml(record.getRemittanceInformationUnstructured())).append("</Ustrd>\n");
        }
        
        xml.append("        </RmtInf>\n");
    }
    
    /**
     * Escape XML special characters
     */
    private String escapeXml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}
