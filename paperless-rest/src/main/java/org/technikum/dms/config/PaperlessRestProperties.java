package org.technikum.dms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "paperlessrest")
public record PaperlessRestProperties(
        RabbitMQProps rabbitmq,
        ElasticsearchProps elasticsearch,
        MinioProps minio
) {

    public PaperlessRestProperties {
        if (rabbitmq == null) {
            rabbitmq = new RabbitMQProps(null, null, null, null);
        }
        if (elasticsearch == null) {
            elasticsearch = new ElasticsearchProps(null);
        }
        if (minio == null) {
            minio = new MinioProps(null, null, null);
        }
    }

     /*
     *  - processingQueue: name of the queue for incoming doc processing (e.g. OCR).
     *  - resultQueue: name of the queue for returning OCR results.
     *  - exchange: name of the RabbitMQ exchange used for publishing messages.
     *  - routingKey: routing key used to bind queues to the exchange.
     *
     * In docker-compose.yml, these typically map to:
     *  PAPERLESSREST_RABBITMQ_PROCESSING_QUEUE
     *  PAPERLESSREST_RABBITMQ_RESULT_QUEUE
     *  PAPERLESSREST_RABBITMQ_EXCHANGE
     *  PAPERLESSREST_RABBITMQ_ROUTING_KEY
     */
    public record RabbitMQProps(
            String processingQueue,
            String resultQueue,
            String exchange,
            String routingKey
    ) {
        public RabbitMQProps {
            if (processingQueue == null) processingQueue = "document_processing_queue";
            if (resultQueue == null)     resultQueue     = "document_result_queue";
            if (exchange == null)        exchange        = "document_exchange";
            if (routingKey == null)      routingKey      = "document_routing_key";
        }
    }

    /**
     *  - uris: The base URL(s) where Elasticsearch is running
     *
     * Typically set via PAPERLESSREST_ELASTICSEARCH_URIS in docker-compose.yml
     */
    public record ElasticsearchProps(String uris) {
        public ElasticsearchProps {
            if (uris == null) uris = "http://elasticsearch:9200";
        }
    }

    /**
     *  - url: Where the MinIO server is located, e.g. http://minio:9000
     *  - accessKey: The MinIO "username" (root user)
     *  - secretKey: The MinIO "password" (root password)
     *
     * Typically set via PAPERLESSREST_MINIO_URL, PAPERLESSREST_MINIO_ACCESS_KEY,
     * and PAPERLESSREST_MINIO_SECRET_KEY in docker-compose.yml
     */
    public record MinioProps(
            String url,
            String accessKey,
            String secretKey
    ) {
        public MinioProps {
            if (url == null)       url       = "http://minio:9000";
            if (accessKey == null) accessKey = "paperless";
            if (secretKey == null) secretKey = "paperless";
        }
    }
}