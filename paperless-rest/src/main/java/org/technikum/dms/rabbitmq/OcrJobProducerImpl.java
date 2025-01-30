package org.technikum.dms.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.technikum.dms.config.PaperlessRestProperties;

/* REST publishes a OCR job to RabbitMQ’s processing queue (step 3c). */
@Service
public class OcrJobProducerImpl implements IOcrJobProducer {
    private final RabbitTemplate rabbitTemplate;
    private final PaperlessRestProperties.RabbitMQProps rabbitProps;

    public OcrJobProducerImpl(
            RabbitTemplate rabbitTemplate,
            PaperlessRestProperties props
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitProps = props.rabbitmq();
    }

    @Override
    public void triggerOcrJob(String documentId, String filename) {
        var message = """
            {
              "type": "OCR_RESULT",
              "documentId": "%s",
              "filename": "%s"
            }
            """.formatted(documentId, filename);
        rabbitTemplate.convertAndSend(rabbitProps.exchange(), rabbitProps.routingKey(), message);
    }
}
