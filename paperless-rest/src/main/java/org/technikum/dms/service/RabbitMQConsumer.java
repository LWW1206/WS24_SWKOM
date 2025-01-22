package org.technikum.dms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.technikum.dms.configs.RabbitMQConfig;


import java.io.InvalidObjectException;

@Service
public class RabbitMQConsumer {

    @Autowired
    private DocumentService documentService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleMessage(FileMessage fileMessage) {
        System.out.println("Received file: " + fileMessage.getFileName());
        System.out.println("Content type: " + fileMessage.getContentType());
        System.out.println("File size: " + fileMessage.getFileData().length);

        CustomMultipartFile multipartFile = new CustomMultipartFile(
                fileMessage.getFileName(),
                fileMessage.getFileName(),
                fileMessage.getContentType(),
                fileMessage.getFileData()
        );

        try {
            documentService.uploadDocument(multipartFile);
            System.out.println("Received file: " + fileMessage.getFileName());
        } catch (InvalidObjectException e) {
            System.err.println("Error processing fileMessage: " + e.getMessage());
        }
        System.out.println("File consumed from RabbitMQ: " + multipartFile.getName());
    }
}
