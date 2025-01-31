package org.technikum.paperless;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.technikum.paperless.service.MessageBroker;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageBrokerTest {

    @Mock private RabbitTemplate rabbitTemplate;
    @InjectMocks private MessageBroker messageBroker;

    private static final String VALID_DOCUMENT_ID = "123";
    private static final String EMPTY_DOCUMENT_ID = "";
    private static final String LONG_DOCUMENT_ID = "A".repeat(1000);

    @BeforeEach
    void setup() {
        messageBroker = new MessageBroker(rabbitTemplate, "resultQueue");
    }

    @Test
    @DisplayName("Send valid message to RabbitMQ")
    void testSendValidMessage() {
        messageBroker.sendToResultQueue(VALID_DOCUMENT_ID);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("resultQueue"),
                eq("{\"documentId\": \"123\"}")
        );
    }

    @Test
    @DisplayName("Send message with empty document ID")
    void testSendMessageWithEmptyDocumentId() {
        messageBroker.sendToResultQueue(EMPTY_DOCUMENT_ID);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("resultQueue"),
                eq("{\"documentId\": \"\"}")
        );
    }

    @Test
    @DisplayName("Send message with long document ID (boundary case)")
    void testSendMessageWithLongDocumentId() {
        messageBroker.sendToResultQueue(LONG_DOCUMENT_ID);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("resultQueue"),
                eq("{\"documentId\": \"" + LONG_DOCUMENT_ID + "\"}")
        );
    }

    @Test
    @DisplayName("Handle RabbitMQ connection failure gracefully")
    void testRabbitMqFailure() {
        doThrow(new RuntimeException("RabbitMQ is down")).when(rabbitTemplate)
                .convertAndSend(anyString(), anyString());

        messageBroker.sendToResultQueue(VALID_DOCUMENT_ID);

        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString());
    }

    @Test
    @DisplayName("Ignore extra fields and always send correct JSON format")
    void testMessageFormatCorrectness() {
        messageBroker.sendToResultQueue(VALID_DOCUMENT_ID);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("resultQueue"),
                matches("\\{\"documentId\": \\\"123\\\"\\}")
        );
    }

    @Test
    @DisplayName("Handle invalid queue name gracefully")
    void testInvalidQueueName() {
        MessageBroker brokerWithInvalidQueue = new MessageBroker(rabbitTemplate, "");

        brokerWithInvalidQueue.sendToResultQueue(VALID_DOCUMENT_ID);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(""),
                eq("{\"documentId\": \"123\"}")
        );
    }
}