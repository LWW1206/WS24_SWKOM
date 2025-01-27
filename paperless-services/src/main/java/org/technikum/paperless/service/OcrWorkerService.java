package org.technikum.paperless.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class OcrWorkerService {

    private final OcrService ocrService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.output}")
    private String outputQueue;

    public OcrWorkerService(OcrService ocrService, RabbitTemplate rabbitTemplate) {
        this.ocrService = ocrService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${queue.input}")
    public void processDocument(String filePath) {
        log.info("Received file path for OCR processing: {}", filePath);

        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                throw new IllegalArgumentException("Invalid file path: " + filePath);
            }

            // Perform OCR processing
            String ocrResult = ocrService.performOcr(file);

            // Log the OCR result or error for debugging
            log.info("OCR processing completed for file: {}. Result: {}", filePath, ocrResult);

            // Send result to the output queue
            rabbitTemplate.convertAndSend(outputQueue, ocrResult);

        } catch (Exception e) {
            // Log error and send it to the output queue for further debugging
            log.error("Error processing file: {} - {}", filePath, e.getMessage(), e);
            String errorMessage = "Error processing file: " + filePath + " - " + e.getMessage();
            rabbitTemplate.convertAndSend(outputQueue, errorMessage);
        }
    }

}
