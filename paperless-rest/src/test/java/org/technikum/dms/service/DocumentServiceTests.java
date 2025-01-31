package org.technikum.dms.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.technikum.dms.dto.DocumentDTO;
import org.technikum.dms.dto.DocumentSearchResultDTO;
import org.technikum.dms.entity.Document;
import org.technikum.dms.rabbitmq.RabbitMQSender;
import org.technikum.dms.repository.DocumentRepository;
import org.technikum.dms.elastic.ElasticsearchSearcher;
import io.minio.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@Disabled("Only run manually once application is running")
public class DocumentServiceTests {

    @MockBean
    private MinioClient minioClient;

    @MockBean
    private DocumentRepository documentRepository;

    @MockBean
    private RabbitMQSender rabbitMQSender;

    @MockBean
    private ElasticsearchSearcher elasticsearchSearcher;

    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(minioClient, documentRepository, rabbitMQSender, elasticsearchSearcher);
    }

    @Test
    @DisplayName("Upload Non-PDF File Should Fail")
    void uploadFile_nonPdf() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png",
                "image/png", "Image Content".getBytes());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            documentService.uploadFile(file);
        });

        assertEquals("Only PDF files are allowed.", exception.getMessage());

        then(documentRepository).shouldHaveNoInteractions();
        then(minioClient).shouldHaveNoInteractions();
        then(rabbitMQSender).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Delete Existing Document Successfully")
    void deleteDocument_success() throws Exception {
        String docId = UUID.randomUUID().toString();
        Document existingDoc = new Document(docId, "test.pdf");

        given(documentRepository.existsById(docId)).willReturn(true);
        given(documentRepository.findById(docId)).willReturn(Optional.of(existingDoc));
        willDoNothing().given(documentRepository).deleteById(docId);
        willDoNothing().given(minioClient).removeObject(any(RemoveObjectArgs.class));

        documentService.deleteDocument(docId);

        then(documentRepository).should().existsById(docId);
        then(documentRepository).should().deleteById(docId);
        then(minioClient).should().removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("Delete Non-Existing Document Should Fail")
    void deleteDocument_notFound() {
        String docId = "nonexistent-id";

        given(documentRepository.existsById(docId)).willReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            documentService.deleteDocument(docId);
        });

        assertEquals("Document not found", exception.getMessage());

        then(documentRepository).should().existsById(docId);
        then(documentRepository).shouldHaveNoMoreInteractions();
        then(minioClient).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Get Document by ID Successfully")
    void getDocumentById_success() {
        String docId = UUID.randomUUID().toString();
        Document doc = new Document(docId, "test.pdf");

        given(documentRepository.findById(docId)).willReturn(Optional.of(doc));

        DocumentDTO result = documentService.getDocumentById(docId);

        assertNotNull(result);
        assertEquals(docId, result.getId());
        assertEquals("test.pdf", result.getFilename());

        then(documentRepository).should().findById(docId);
    }

    @Test
    @DisplayName("Get Document by ID Not Found")
    void getDocumentById_notFound() {
        String docId = "nonexistent-id";

        given(documentRepository.findById(docId)).willReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            documentService.getDocumentById(docId);
        });

        assertEquals("Document not found with ID: " + docId, exception.getMessage());

        then(documentRepository).should().findById(docId);
    }

    @Test
    @DisplayName("Search Documents Successfully")
    void searchDocuments_success() {
        String query = "communication";
        List<DocumentSearchResultDTO> searchResults = List.of(
                new DocumentSearchResultDTO("id1", "Some OCR text about communication", "file1.pdf", "application/pdf", 1024, true, LocalDateTime.now(), "@timestamp")
        );

        given(elasticsearchSearcher.searchDocuments(query)).willReturn(searchResults);
        given(documentRepository.findById("id1")).willReturn(Optional.of(new Document("id1", "file1.pdf")));

        List<DocumentDTO> result = documentService.searchDocuments(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("id1", result.get(0).getId());
        assertEquals("file1.pdf", result.get(0).getFilename());

        then(elasticsearchSearcher).should().searchDocuments(query);
        then(documentRepository).should().findById("id1");
    }
}