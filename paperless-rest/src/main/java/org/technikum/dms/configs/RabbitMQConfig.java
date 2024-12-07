package org.technikum.dms.Configs;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@Configuration
public class RabbitMQConfig {

    public static final String OCR_QUEUE = "ocrQueue";

    @Bean
    public Queue ocrQueue() {
        return new Queue(OCR_QUEUE, false);
    }
}