package org.technikum.dms.rabbitmq;

import org.springframework.context.annotation.Bean;

public interface IOcrJobProducer {
    void triggerOcrJob(String documentId, String filename);
}
