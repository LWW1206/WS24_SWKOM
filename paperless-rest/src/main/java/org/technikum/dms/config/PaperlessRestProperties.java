package org.technikum.dms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * A "master" record capturing all environment-driven config for the Paperless REST project.
 * <p>
 *
 * Spring Boot uses "relaxed binding" for environment variables, which means:
 *  - Environment variable PAPERLESSREST_RABBITMQ_EXCHANGE
 *    maps to Java property paperlessrest.rabbitmq.exchange
 *  - Environment variable PAPERLESSREST_ELASTICSEARCH_URIS
 *    maps to Java property paperlessrest.elasticsearch.uris
 *  - Environment variable PAPERLESSREST_MINIO_URL
 *    maps to Java property paperlessrest.minio.url
 *  - ...and so on.
 * <p>
 * If an environment variable is missing, the default values specified in each nested record's
 * constructor will be used instead.
 * <p>
 * For example, in the docker-compose.yml, you can define:
 *  PAPERLESSREST_RABBITMQ_EXCHANGE=document_exchange
 *  PAPERLESSREST_RABBITMQ_ROUTING_KEY=document_routing_key
 *  etc...
 */
@ConfigurationProperties(prefix = "paperlessrest")
public record PaperlessRestProperties(
        RabbitMQProps rabbitmq,
        ElasticsearchProps elasticsearch,
        MinioProps minio
) {

    /**
     * Compact constructor ensures we have non-null sub-records
     * even if not all environment variables are set.
     */
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

    // ========================================================================
    // 1) Nested record for RabbitMQ config
    // ========================================================================
    /**
     * Holds RabbitMQ-specific configuration:
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
        /**
         * Compact constructor that provides default strings if the environment
         * variables are not set.
         */
        public RabbitMQProps {
            if (processingQueue == null) processingQueue = "document_processing_queue";
            if (resultQueue == null)     resultQueue     = "document_result_queue";
            if (exchange == null)        exchange        = "document_exchange";
            if (routingKey == null)      routingKey      = "document_routing_key";
        }
    }

    // ========================================================================
    // 2) Nested record for Elasticsearch config
    // ========================================================================
    /**
     * Holds Elasticsearch-specific config:
     *  - uris: The base URL(s) where Elasticsearch is running
     *
     * Typically set via PAPERLESSREST_ELASTICSEARCH_URIS in docker-compose.yml
     */
    public record ElasticsearchProps(String uris) {
        /**
         * Provide a default URI if not set, e.g. http://elasticsearch:9200
         */
        public ElasticsearchProps {
            if (uris == null) uris = "http://elasticsearch:9200";
        }
    }

    // ========================================================================
    // 3) Nested record for MinIO config
    // ========================================================================
    /**
     * Holds MinIO-specific config:
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
        /**
         * Provide default MinIO server credentials if environment
         * variables are not set.
         */
        public MinioProps {
            if (url == null)       url       = "http://minio:9000";
            if (accessKey == null) accessKey = "paperless";
            if (secretKey == null) secretKey = "paperless";
        }
    }
}