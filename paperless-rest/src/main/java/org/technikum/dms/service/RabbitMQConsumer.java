package org.technikum.dms.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.technikum.dms.configs.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class RabbitMQConsumer{
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleMessage(FileMessage fileMessage){
        logger.info("Received FileMessageName: {}", fileMessage.getFileName());

        CustomMultipartFile multipartFile = new CustomMultipartFile(
                fileMessage.getFileName(),
                fileMessage.getFileName(),
                fileMessage.getContentType(),
                fileMessage.getFileData()
        );

        try {
            //ocr
            logger.debug("Processing message: {}", fileMessage.getFileName());
        } catch (Exception e) {
            logger.error("Error while processing message: {}", e.getMessage(), e);
        }
    }
}
