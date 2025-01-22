package org.technikum.dms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.technikum.dms.entity.Document;
import org.technikum.dms.repository.DocumentRepository;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document uploadDocument(MultipartFile file) throws InvalidObjectException {
        if (file == null || file.isEmpty()) {
            throw new InvalidObjectException("File is empty. Please upload a valid file.");
        }

        String filename = file.getOriginalFilename();
        String filetype = file.getContentType();

        if (filetype == null || !filetype.equals("application/pdf") ||
                (filename != null && !filename.toLowerCase().endsWith(".pdf"))) {
            throw new InvalidObjectException("Invalid file type. Only PDF files are allowed.");
        }

        Document document = new Document();
        try {
            document.setName(filename);
            document.setFile(file.getBytes());
            document.setCreatedAt(LocalDateTime.now());
        } catch (IOException e) {
            throw new InvalidObjectException("Failed to read file content.");
        }

        return documentRepository.save(document);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Optional<Document> byId(int documentId) {
        return documentRepository.findById(documentId);
    }
}
