package org.technikum.paperless.configs;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import io.minio.MinioClient;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaperlessConfig {
    @Bean
    public MinioClient minioClient(
            @Value("${minio.url}") String minioUrl,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey) {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    public Queue processingQueue(@Value("${rabbitmq.processing.queue}") String queueName) {
        return new Queue(queueName);
    }

    @Bean
    public Queue resultQueue(@Value("${rabbitmq.result.queue}") String queueName) {
        return new Queue(queueName);
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        RestClient restClient = RestClient.builder(
                new HttpHost("elasticsearch", 9200, "http")
        ).build();

        return new ElasticsearchClient(
                new RestClientTransport(restClient, new JacksonJsonpMapper())
        );
    }
}