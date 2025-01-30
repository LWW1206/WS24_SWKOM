package org.technikum.dms.rabbitmq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technikum.dms.repository.DocumentRepository;


@Component
public class OcrResultListener {
    private static final Logger logger = LoggerFactory.getLogger(OcrResultListener.class);
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;

    public OcrResultListener(DocumentRepository documentRepository, ObjectMapper objectMapper) {
        this.documentRepository = documentRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${paperlessrest.rabbitmq.result_queue}")
    public void handleMessage(String rawMessage) {
        try {
            JsonNode json = objectMapper.readTree(rawMessage);
            String docId = json.get("documentId").asText();

            documentRepository.findById(docId).ifPresent(doc -> {
                doc.setOcrJobDone(true);
                documentRepository.save(doc);
                logger.info("Updated OCR status for document: {}", docId);
            });
        } catch (Exception e) {
            logger.error("Error processing OCR result: {}", e.getMessage(), e);
        }
    }
}
