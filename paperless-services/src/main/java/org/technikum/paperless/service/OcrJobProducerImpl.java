package org.technikum.paperless.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.technikum.paperless.configs.PaperlessWorkerProperties;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OcrJobProducerImpl {
    private final RabbitTemplate rabbitTemplate;
    private final PaperlessWorkerProperties.RabbitMQProps rabbitProps;
    private final ObjectMapper objectMapper;

    public OcrJobProducerImpl(
            RabbitTemplate rabbitTemplate,
            PaperlessWorkerProperties props
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitProps = props.rabbitmq();
        this.objectMapper = new ObjectMapper();
    }

    public void sendToResultQueue(String documentId, String ocrText) {
        try {
            Map<String, String> message = new HashMap<>();
            message.put("documentId", documentId);
            message.put("ocrText", ocrText);

            String jsonString = objectMapper.writeValueAsString(message);
            log.debug("Sending message: {}", jsonString);

            rabbitTemplate.convertAndSend(rabbitProps.resultQueue(), jsonString);
        } catch (Exception e) {
            log.error("Failed to send OCR result for document {}: {}", documentId, e.getMessage(), e);
        }
    }
}