package org.technikum.paperless.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageBroker {
    private final RabbitTemplate rabbitTemplate;
    private final String resultQueue;

    public MessageBroker(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.result.queue}") String resultQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.resultQueue = resultQueue;
    }

    public void sendToResultQueue(String documentId) {
        try {
            rabbitTemplate.convertAndSend(
                    resultQueue,
                    String.format("{\"documentId\": \"%s\"}", documentId)
            );
        } catch (Exception e) {
            log.error("Failed sending result for document {}: {}", documentId, e.getMessage());
        }
    }
}