package org.technikum.dms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "paperlessrest")
public record PaperlessRestProperties(
        RabbitMQProps rabbitmq,
        ElasticsearchProps elasticsearch,
        MinioProps minio
) {
    public PaperlessRestProperties {
        if (rabbitmq == null) rabbitmq = new RabbitMQProps(null, null, null, null);
        if (elasticsearch == null) elasticsearch = new ElasticsearchProps(null);
        if (minio == null) minio = new MinioProps(null, null, null);
    }

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

    public record ElasticsearchProps(String uris) {
        public ElasticsearchProps {
            if (uris == null) uris = "http://elasticsearch:9200";
        }
    }

    public record MinioProps(String url, String accessKey, String secretKey) {
        public MinioProps {
            if (url == null)       url       = "http://minio:9000";
            if (accessKey == null) accessKey = "paperless";
            if (secretKey == null) secretKey = "paperless";
        }
    }
}
