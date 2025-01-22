package org.technikum.dms.service;

import org.technikum.dms.configs.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMultipartFile(FileMessage file) {

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, file);

        System.out.println("File sent to RabbitMQ: " + file.getFileName());
    }
}