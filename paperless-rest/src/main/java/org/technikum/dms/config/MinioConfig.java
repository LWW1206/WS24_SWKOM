package org.technikum.dms.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${MINIO_URL:http://minio:9000}")
    private String minioUrl;

    @Value("${MINIO_ACCESS_KEY:paperless}")
    private String accessKey;

    @Value("${MINIO_SECRET_KEY:paperless}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
    }
}