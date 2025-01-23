package org.technikum.paperless.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

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
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                throw new IllegalArgumentException("Invalid file path: " + filePath);
            }

            // Perform OCR processing
            String ocrResult = ocrService.performOcr(file);

            // Send result to the output queue
            rabbitTemplate.convertAndSend(outputQueue, ocrResult);

        } catch (Exception e) {
            // Send error message to the output queue for further debugging
            String errorMessage = "Error processing file: " + filePath + " - " + e.getMessage();
            rabbitTemplate.convertAndSend(outputQueue, errorMessage);
        }
    }
}
