# Payment Builder Service Components

## Overview

The Payment Builder service processes CSV files containing payment information and generates ISO20022 compliant XML payment messages.

## Component Architecture

### 1. Configuration Layer

**PaymentBuilderProperties** (`config/PaymentBuilderProperties.java`)
- Loads configuration from `application.yaml`
- Provides input/output directory paths
- Supports environment variable overrides

### 2. Model Layer

**PaymentRecord** (`model/PaymentRecord.java`)
- Represents a single payment transaction
- Contains debtor, creditor, and payment information
- Maps to ISO20022 message fields

### 3. Service Layer

**CsvParserService** (`service/CsvParserService.java`)
- Parses CSV files into PaymentRecord objects
- Supports flexible header naming
- Validates data types (amounts, dates)

**PaymentMessageGeneratorService** (`service/PaymentMessageGeneratorService.java`)
- Generates ISO20022 pain.013.001.11 XML messages
- Handles XML escaping and formatting
- Validates required fields

**PaymentBuilderService** (`service/PaymentBuilderService.java`)
- Orchestrates the complete workflow
- Processes all CSV files in input directory
- Writes XML output to output directory
- Provides logging and error handling

## Processing Flow

```
1. Read CSV files from input directory
2. Parse each CSV file into PaymentRecord objects
3. Generate ISO20022 XML message for each file
4. Write XML output to output directory
5. Log processing results
```

## ISO20022 Message Support

### Currently Implemented
- **pain.013.001.11** - Creditor Payment Activation Request

### XSD Schemas Available
- `pain.013.001.11.xsd` - Creditor Payment Activation Request
- `pain.014.001.11.xsd` - Creditor Payment Activation Request Status Report

## CSV Format

See `CSV_FIELD_MAPPING.md` for complete field documentation.

Sample CSV template: `csv-template.csv`

## Configuration

Configure input/output directories in `application.yaml`:

```yaml
payment:
  builder:
    input:
      directory: ./input
    output:
      directory: ./output
```

Or use environment variables:
- `INPUT_DIR` - Override input directory
- `OUTPUT_DIR` - Override output directory

## Usage

1. Place CSV files in the configured input directory
2. Run the application
3. Find generated XML files in the output directory

## Field Mapping Features

- Flexible CSV header naming (supports multiple variations)
- Automatic data type conversion (strings, decimals, dates)
- Optional and required field validation
- XML special character escaping
- Unique message and transaction ID generation
