package org.technikum.dms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.technikum.dms.configs.RabbitMQConfig;

import java.io.InvalidObjectException;

@Service
@Slf4j
public class RabbitMQConsumer {
    private final DocumentService documentService;

    public RabbitMQConsumer(DocumentService documentService) {
        this.documentService = documentService;
    }

    @RabbitListener(queues = "${queue.input}")
    public void handleMessage(FileMessage fileMessage) {
        log.info("Received file: {} with size: {}", fileMessage.getFileName(), fileMessage.getFileData().length);

        CustomMultipartFile multipartFile = new CustomMultipartFile(
                fileMessage.getFileName(),
                fileMessage.getFileName(),
                fileMessage.getContentType(),
                fileMessage.getFileData()
        );

        try {
            log.info("Processing file: {}", fileMessage.getFileName());
            documentService.uploadDocument(multipartFile);
            log.info("Successfully processed file: {}", fileMessage.getFileName());
        } catch (Exception e) {
            log.error("Error processing file {}: {}", fileMessage.getFileName(), e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Failed to process message", e);
        }
    }

}