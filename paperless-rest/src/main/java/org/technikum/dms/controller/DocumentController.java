package org.technikum.dms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.technikum.dms.entity.DocumentDTO;
import org.technikum.dms.service.DocumentService;
import org.technikum.dms.service.RabbitMQSender;
import org.technikum.dms.entity.Document;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @PostMapping
    public ResponseEntity<String> uploadDocument(@RequestBody DocumentDTO documentDTO) {
        Document savedDocument = documentService.saveDocument(documentDTO);

        return ResponseEntity.ok("Document uploaded successfully with ID: " + savedDocument.getId());
    }

    @PostMapping("/send-to-queue")
    public String sendToQueue(@RequestBody String document) {
        rabbitMQSender.sendMessage(document);
        return "Document sent to OCR queue";
    }
}
