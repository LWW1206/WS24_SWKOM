package org.technikum.dms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.technikum.dms.entity.Document;
import org.technikum.dms.entity.DocumentDTO;
import org.technikum.dms.service.DocumentService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping
    public ResponseEntity<String> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name
    ) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file provided");
        }

        try {
            DocumentDTO documentDTO = new DocumentDTO();
            documentDTO.setName(name != null ? name : file.getOriginalFilename());

            documentDTO.setContent(file.getBytes());

            documentService.uploadDocument(file);

            return ResponseEntity.ok("Document uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to process the file");
        }
    }

    @GetMapping
    public ResponseEntity getAllDocuments(){
        List<Document> documents = documentService.getAllDocuments();
        if (documents.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity downloadDocument(@PathVariable int documentId) {
        Optional<Document> document = documentService.byId(documentId);

        if (document.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + document.get().getName() + "\"")
                .header("Content-Type", "application/octet-stream")
                .body(document.get().getFile());
    }
}
