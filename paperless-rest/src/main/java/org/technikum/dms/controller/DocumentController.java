package org.technikum.dms.controller;

import org.technikum.dms.dto.DocumentDTO;
import org.technikum.dms.dto.DocumentWithFileDTO;
import org.technikum.dms.entity.Document;
import org.technikum.dms.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Operation(summary = "Uploading a document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document upload successful", content = @Content(schema = @Schema(implementation = Document.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file format / bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            DocumentDTO document = documentService.uploadFile(file);
            return ResponseEntity.ok().body(document);
        } catch (IllegalArgumentException e) {
            log.error("Invalid file format: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error while uploading document: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Deletes a document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document deletion successful"),
            @ApiResponse(responseCode = "404", description = "Document not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Document not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error while deleting document with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get a document by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document retrieved successfully", content = @Content(schema = @Schema(implementation = Document.class))),
            @ApiResponse(responseCode = "404", description = "Document not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> getDocumentById(@PathVariable String id) {
        try {
            DocumentDTO document = documentService.getDocumentById(id);
            byte[] pdfFile = documentService.getDocumentFileById(id);

            if (pdfFile == null || document == null) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", document.getFilename());
            headers.setContentType(MediaType.APPLICATION_PDF);

            return ResponseEntity.ok().headers(headers).body(pdfFile);
        } catch (IllegalArgumentException e) {
            log.error("Document not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error while retrieving the document with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get all documents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All documents retrieved successfully", content = @Content(schema = @Schema(implementation = DocumentDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        try {
            List<DocumentDTO> documents = documentService.getAllDocuments();
            log.info("Found {} documents", documents.size());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Error retrieving documents: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Searches documents with a query and fetches additional data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DocumentWithFileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid query"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<List<DocumentDTO>> searchDocuments(@RequestParam String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                log.error("Invalid query parameter: query cannot be null or empty");
                return ResponseEntity.badRequest().build();
            }

            log.info("Searching documents with the query: {}", query);
            List<DocumentDTO> results = documentService.searchDocuments(query);
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            log.error("Error occurred while searching documents: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}