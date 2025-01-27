package org.technikum.dms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.technikum.dms.configs.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j  // Lombok annotation for logging
public class RabbitMQSender {
    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.input}")
    private String inputQueue;

    public RabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMultipartFile(FileMessage file) {
        log.info("Sending file: {} with size: {}", file.getFileName(), file.getFileData().length);
        rabbitTemplate.convertAndSend(inputQueue, file);
        log.info("File sent to processing: {}", file.getFileName());
    }
}