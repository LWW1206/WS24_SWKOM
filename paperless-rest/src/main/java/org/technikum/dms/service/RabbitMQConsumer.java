package org.technikum.dms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.technikum.dms.service.FileMessage;
import org.technikum.dms.service.CustomMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.technikum.dms.Configs.RabbitMQConfig;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.io.InvalidObjectException;

@Service
public class RabbitMQConsumer{
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    @Autowired
    private DocumentService documentService;

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
