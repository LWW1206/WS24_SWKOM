package org.technikum.paperless.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DocumentProcessor {
    private final MinioClient minioClient;
    private final ElasticsearchClient elasticsearchClient;
    private final MessageBroker messageBroker;
    private final Tesseract tesseract;

    public DocumentProcessor(MinioClient minioClient, ElasticsearchClient elasticsearchClient,
                             MessageBroker messageBroker) {
        this.minioClient = minioClient;
        this.elasticsearchClient = elasticsearchClient;
        this.messageBroker = messageBroker;
        this.tesseract = initTesseract();
    }

    @RabbitListener(queues = "${rabbitmq.processing.queue}")
    public void processDocument(String message) {
        JSONObject jsonMessage = new JSONObject(message);
        String documentId = jsonMessage.getString("documentId");
        String filename = jsonMessage.getString("filename");

        try {
            File tempFile = downloadFromStorage(documentId);
            String extractedText = performOcr(tempFile);
            indexToElastic(documentId, filename, extractedText);
            messageBroker.sendToResultQueue(documentId);
        } catch (Exception e) {
            log.error("Document processing failed: {}", e.getMessage(), e);
        }
    }

    private File downloadFromStorage(String documentId) throws Exception {
        InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("documents")
                        .object(documentId)
                        .build()
        );

        File tempFile = new File("/tmp/" + documentId + ".pdf");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            inputStream.transferTo(fos);
        }
        return tempFile;
    }

    private String performOcr(File file) throws Exception {
        return tesseract.doOCR(file);
    }

    private String indexToElastic(String documentId, String filename, String ocrText) throws Exception {
        Map<String, Object> document = new HashMap<>();
        document.put("documentId", documentId);
        document.put("filename", filename);
        document.put("ocrText", ocrText);
        document.put("@timestamp", Instant.now().toString());

        IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                .index("documents")
                .id(documentId)
                .document(document)
        );

        IndexResponse response = elasticsearchClient.index(request);
        return response.id();
    }

    private Tesseract initTesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
        tesseract.setLanguage("eng");
        return tesseract;
    }
}