package org.technikum.dms.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitMQSender {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOCRJobMessage(String documentId, String filename) {
        String message = "{\"documentId\":\"" + documentId + "\", \"filename\":\"" + filename + "\"}";
        log.info("Preparing to send OCR job message for document ID: {}", documentId);

        try {
            rabbitTemplate.convertAndSend(org.technikum.dms.rabbitmq.RabbitMQConfig.EXCHANGE, org.technikum.dms.rabbitmq.RabbitMQConfig.ROUTING_KEY, message);
            log.info("Message successfully sent to RabbitMQ for document ID: {}", documentId);
        } catch (Exception e) {
            log.error("Failed to send message to RabbitMQ for document ID: {}", documentId, e);
        }
    }
}