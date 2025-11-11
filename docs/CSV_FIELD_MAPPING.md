# CSV Field Mapping Documentation

This document describes the mapping between CSV input fields and ISO20022 pain.013.001.11 payment message fields.

## CSV Format

The CSV file should contain a header row with column names, followed by data rows. The service supports flexible header naming (see alternative names below).

## Field Mappings

### Debtor (Payer) Information

| CSV Field | Alternative Names | ISO20022 Element | Required | Description |
|-----------|------------------|------------------|----------|-------------|
| debtor_name | debtorname, payer_name | Dbtr/Nm | Yes | Name of the payer |
| debtor_iban | debtor_account_iban, payer_iban | DbtrAcct/Id/IBAN | Yes* | IBAN of debtor account |
| debtor_account_other | debtor_account | DbtrAcct/Id/Othr | Yes* | Other account identifier (if no IBAN) |
| debtor_bic | payer_bic | DbtrAgt/FinInstnId/BICFI | No | BIC of debtor's bank |
| debtor_address_line1 | debtor_address1 | Dbtr/PstlAdr/AdrLine | No | First line of debtor address |
| debtor_address_line2 | debtor_address2 | Dbtr/PstlAdr/AdrLine | No | Second line of debtor address |
| debtor_country | payer_country | Dbtr/PstlAdr/Ctry | No | Two-letter country code |

*Either IBAN or Other account identifier must be provided

### Creditor (Payee) Information

| CSV Field | Alternative Names | ISO20022 Element | Required | Description |
|-----------|------------------|------------------|----------|-------------|
| creditor_name | creditorname, payee_name | Cdtr/Nm | Yes | Name of the payee |
| creditor_iban | creditor_account_iban, payee_iban | CdtrAcct/Id/IBAN | Yes* | IBAN of creditor account |
| creditor_account_other | creditor_account | CdtrAcct/Id/Othr | Yes* | Other account identifier (if no IBAN) |
| creditor_bic | payee_bic | CdtrAgt/FinInstnId/BICFI | No | BIC of creditor's bank |
| creditor_address_line1 | creditor_address1 | Cdtr/PstlAdr/AdrLine | No | First line of creditor address |
| creditor_address_line2 | creditor_address2 | Cdtr/PstlAdr/AdrLine | No | Second line of creditor address |
| creditor_country | payee_country | Cdtr/PstlAdr/Ctry | No | Two-letter country code |

*Either IBAN or Other account identifier must be provided

### Payment Information

| CSV Field | Alternative Names | ISO20022 Element | Required | Description |
|-----------|------------------|------------------|----------|-------------|
| amount | instructed_amount, payment_amount | Amt/InstdAmt | Yes | Payment amount (decimal) |
| currency | ccy | Amt/InstdAmt/@Ccy | Yes | Three-letter currency code (e.g., EUR, USD) |
| execution_date | requested_execution_date, payment_date | ReqdExctnDt | No | Date in yyyy-MM-dd format |
| end_to_end_id | endtoendid, reference | PmtId/EndToEndId | No | Unique end-to-end reference |
| instruction_id | instructionid | PmtId/InstrId | No | Instruction identification |

### Remittance Information

| CSV Field | Alternative Names | ISO20022 Element | Required | Description |
|-----------|------------------|------------------|----------|-------------|
| remittance_info | remittance_information, payment_reference | RmtInf/Ustrd | No | Unstructured remittance information |
| remittance_structured | structured_remittance | RmtInf/Strd | No | Structured remittance information |

### Additional Fields

| CSV Field | Alternative Names | ISO20022 Element | Required | Description |
|-----------|------------------|------------------|----------|-------------|
| purpose_code | purpose | Purp/Cd | No | Purpose code (e.g., SALA for salary) |
| category_purpose_code | category_purpose | PmtTpInf/CtgyPurp/Cd | No | Category purpose code |
| charge_bearer | charges | ChrgBr | No | Charge bearer (SLEV, SHAR, CRED, DEBT) |

## Example CSV

See `csv-template.csv` for a complete example with sample data.

## Notes

- All date fields should be in ISO format: yyyy-MM-dd
- Amount fields should use decimal notation (e.g., 1000.50)
- Currency codes should be 3-letter ISO codes (e.g., EUR, USD, GBP)
- Country codes should be 2-letter ISO codes (e.g., DE, FR, GB)
- BIC codes should follow the standard format (8 or 11 characters)
- IBAN format depends on the country

## Supported ISO20022 Message Types

Currently, the service generates:
- **pain.013.001.11** - Creditor Payment Activation Request

The XSD schemas are available in the `/docs` folder for reference.
