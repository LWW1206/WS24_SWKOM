package org.technikum.dms.config;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import java.util.HashMap;
import java.util.Map;

@EnableRetry
@Configuration
public class RabbitConfig {

    @Bean
    public Queue processingQueue(PaperlessRestProperties props) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-queue-type", "classic");
        return new Queue(
                props.rabbitmq().processingQueue(),
                true,
                false,
                false,
                arguments
        );
    }

    @Bean
    public Queue resultQueue(PaperlessRestProperties props) {
        return new Queue(
                props.rabbitmq().resultQueue(),
                true,
                false,
                false
        );
    }

    @Bean
    public DirectExchange documentExchange(PaperlessRestProperties props) {
        // Make exchange durable
        return new DirectExchange(props.rabbitmq().exchange(), true, false);
    }

    @Bean
    public Binding processingBinding(
            Queue processingQueue,
            DirectExchange documentExchange,
            PaperlessRestProperties props
    ) {
        return BindingBuilder.bind(processingQueue)
                .to(documentExchange)
                .with(props.rabbitmq().routingKey());
    }

    @Bean
    public Binding resultBinding(
            Queue resultQueue,
            DirectExchange documentExchange,
            PaperlessRestProperties props
    ) {
        return BindingBuilder.bind(resultQueue)
                .to(documentExchange)
                .with(props.rabbitmq().routingKey() + ".result");
    }
}