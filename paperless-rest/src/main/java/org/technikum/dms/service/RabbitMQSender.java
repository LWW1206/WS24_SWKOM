package org.technikum.dms.service;

import org.technikum.dms.configs.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RabbitMQSender {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMultipartFile(FileMessage file){

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, file);

        logger.info("File sent to RabbitMQ: {}", file.getFileName());
    }
}