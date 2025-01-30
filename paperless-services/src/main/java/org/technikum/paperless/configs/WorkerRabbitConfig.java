package org.technikum.paperless.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declares the Worker’s queues and any needed exchange/bindings.
 * Spring Boot will auto-declare them via RabbitAdmin at startup.
 */
@Configuration
public class WorkerRabbitConfig {

    @Bean
    public Queue processingQueue(PaperlessWorkerProperties props) {
        return new Queue(
                props.rabbitmq().processingQueue(),
                true,
                false,
                false
        );
    }
}
