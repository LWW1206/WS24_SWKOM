package org.technikum.dms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.technikum.dms.entity.DocumentDTO;
import org.technikum.dms.service.DocumentService;

import java.io.IOException;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping
    public ResponseEntity<String> uploadDocument(
            @RequestParam("file") MultipartFile file, // Handles file upload
            @RequestParam(value = "name", required = false) String name) { // Optional name parameter

        // Check if the file is empty
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file provided");
        }

        try {
            // Create DocumentDTO object
            DocumentDTO documentDTO = new DocumentDTO();
            documentDTO.setName(name != null ? name : file.getOriginalFilename()); // Set name from filename or provided name

            // Store the file content as byte array
            documentDTO.setContent(file.getBytes());  // Convert MultipartFile content to byte array

            // Save the document using the service layer
            documentService.uploadDocument(file);

            // Return success response
            return ResponseEntity.ok("Document uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to process the file");
        }
    }
}
