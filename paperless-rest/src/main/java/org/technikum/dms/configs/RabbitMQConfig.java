package org.technikum.dms.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.technikum.dms.service.RabbitMQConsumer;

@EnableRetry
@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "File.Queue";

    @Bean
    public Queue ocrQueue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(
            ConnectionFactory connectionFactory,
            RabbitMQConsumer rabbitMQConsumer) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("File.Queue");
        container.setMessageListener(new MessageListenerAdapter(rabbitMQConsumer, messageConverter()));
        return container;
    }
}