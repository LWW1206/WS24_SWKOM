package org.technikum.dms.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.technikum.dms.service.RabbitMQConsumer;

@Configuration
public class RabbitMQConfig {
    @Value("${queue.input}")
    private String inputQueue;

    @Value("${queue.output}")
    private String outputQueue;

    @Bean
    public Queue inputQueue() {
        return new Queue(inputQueue, true);
    }

    @Bean
    public Queue outputQueue() {
        return new Queue(outputQueue, true);
    }

}