package org.technikum.dms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.technikum.dms.entity.Document;
import org.technikum.dms.entity.DocumentDTO;
import org.technikum.dms.service.DocumentService;
import org.technikum.dms.service.RabbitMQSender;
import org.technikum.dms.entity.FileMessage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @PostMapping
    @Operation(summary = "Upload a document", description = "Uploads a document to database and sends it to RabbitMQ.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Document uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or missing file"),
            @ApiResponse(responseCode = "500", description = "Failed to process the file")
    })
    public ResponseEntity<String> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file provided");
        }

        try {
            DocumentDTO documentDTO = new DocumentDTO();
            documentDTO.setName(name != null ? name : file.getOriginalFilename());

            documentDTO.setContent(file.getBytes());

            FileMessage fileMessage = new FileMessage(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );

            rabbitMQSender.sendMultipartFile(fileMessage);

            return ResponseEntity.ok("Document uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to process the file");
        }
    }

    @GetMapping
    @Operation(summary = "Get all document", description = "Gets all documents from the database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documents returned successfully"),
            @ApiResponse(responseCode = "204", description = "No documents in database")
    })
    public ResponseEntity getAllDocuments(){
        List<Document> documents = documentService.getAllDocuments();
        if (documents.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/download/{documentId}")
    @Operation(summary = "Download a document", description = "Downloads a document with the specified ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Document found and returned successfully"),
            @ApiResponse(responseCode = "204", description = "No documents with specified ID found")
    })
    @Parameter(name = "documentId", description = "ID of the document to download", required = true)
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
