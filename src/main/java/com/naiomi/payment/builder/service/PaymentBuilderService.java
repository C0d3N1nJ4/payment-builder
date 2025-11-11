package com.naiomi.payment.builder.service;

import com.naiomi.payment.builder.config.PaymentBuilderProperties;
import com.naiomi.payment.builder.model.PaymentRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * Main service for processing CSV files and generating ISO20022 payment messages
 */
@Service
public class PaymentBuilderService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentBuilderService.class);
    
    private final PaymentBuilderProperties properties;
    private final CsvParserService csvParserService;
    private final PaymentMessageGeneratorService messageGeneratorService;
    
    public PaymentBuilderService(
            PaymentBuilderProperties properties,
            CsvParserService csvParserService,
            PaymentMessageGeneratorService messageGeneratorService) {
        this.properties = properties;
        this.csvParserService = csvParserService;
        this.messageGeneratorService = messageGeneratorService;
    }
    
    /**
     * Process all CSV files in the input directory
     * 
     * @return Number of files processed
     * @throws IOException if file operations fail
     */
    public int processInputFiles() throws IOException {
        Path inputDir = Paths.get(properties.getInput().getDirectory());
        Path outputDir = Paths.get(properties.getOutput().getDirectory());
        
        // Create directories if they don't exist
        Files.createDirectories(inputDir);
        Files.createDirectories(outputDir);
        
        logger.info("Processing CSV files from: {}", inputDir.toAbsolutePath());
        logger.info("Output directory: {}", outputDir.toAbsolutePath());
        
        int processedCount = 0;
        
        try (Stream<Path> paths = Files.walk(inputDir, 1)) {
            List<Path> csvFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".csv"))
                    .toList();
            
            logger.info("Found {} CSV file(s) to process", csvFiles.size());
            
            for (Path csvFile : csvFiles) {
                try {
                    processFile(csvFile, outputDir);
                    processedCount++;
                    logger.info("Successfully processed: {}", csvFile.getFileName());
                } catch (Exception e) {
                    logger.error("Error processing file {}: {}", csvFile.getFileName(), e.getMessage(), e);
                }
            }
        }
        
        logger.info("Processing complete. {} file(s) processed successfully", processedCount);
        return processedCount;
    }
    
    /**
     * Process a single CSV file
     * 
     * @param csvFile Path to the CSV file
     * @param outputDir Output directory for generated XML
     * @throws IOException if file operations fail
     */
    public void processFile(Path csvFile, Path outputDir) throws IOException {
        logger.info("Parsing CSV file: {}", csvFile.getFileName());
        
        // Parse CSV file
        List<PaymentRecord> records = csvParserService.parseCsvFile(csvFile);
        logger.info("Parsed {} payment record(s)", records.size());
        
        if (records.isEmpty()) {
            logger.warn("No records found in file: {}", csvFile.getFileName());
            return;
        }
        
        // Generate payment message XML
        String xmlContent = messageGeneratorService.generatePaymentMessage(records);
        
        // Write output file
        String outputFileName = getOutputFileName(csvFile);
        Path outputFile = outputDir.resolve(outputFileName);
        Files.writeString(outputFile, xmlContent);
        
        logger.info("Generated payment message: {}", outputFile.getFileName());
    }
    
    /**
     * Generate output file name from input file name
     * 
     * @param inputFile Input CSV file path
     * @return Output XML file name
     */
    private String getOutputFileName(Path inputFile) {
        String fileName = inputFile.getFileName().toString();
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        return baseName + "_pain013.xml";
    }
}
