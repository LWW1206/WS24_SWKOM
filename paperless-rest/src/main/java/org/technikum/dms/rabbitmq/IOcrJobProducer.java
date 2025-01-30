package org.technikum.dms.rabbitmq;

public interface IOcrJobProducer {
    void triggerOcrJob(String documentId, String filename);
}
