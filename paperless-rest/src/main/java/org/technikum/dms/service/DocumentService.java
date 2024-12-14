package org.technikum.dms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.technikum.dms.entity.Document;
import org.technikum.dms.repository.DocumentRepository;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    // Upload the document and save it
    public Document uploadDocument(MultipartFile file) throws InvalidObjectException {
        // Validate the file
        if (file == null || file.isEmpty()) {
            throw new InvalidObjectException("File is empty. Please upload a valid file.");
        }

        String filename = file.getOriginalFilename();
        String filetype = file.getContentType();

        // Validate MIME type and file extension (only PDF files are allowed in this example)
        if (filetype == null || !filetype.equals("application/pdf") ||
                (filename != null && !filename.toLowerCase().endsWith(".pdf"))) {
            throw new InvalidObjectException ("Invalid file type. Only PDF files are allowed.");
        }

        // Create a new Document object
        Document document = new Document();
        try {
            //document.setId(Long.valueOf(UUID.randomUUID().toString()));  // Generate a new ID (UUID)
            document.setName(filename);  // Set the filename
            document.setFile(file.getBytes());  // Set the file content as byte array
            document.setCreatedAt(LocalDateTime.now());  // Set the upload date
        } catch (IOException e) {
            throw new InvalidObjectException("Failed to read file content.");
        }

        // Save the document to the database and return it
        return documentRepository.save(document);
    }
}
