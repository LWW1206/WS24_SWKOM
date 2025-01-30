package org.technikum.dms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.technikum.dms.dto.DocumentDTO;
import org.technikum.dms.service.DocumentServiceImpl;
import org.technikum.dms.service.IDocumentService;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class DocumentController {
    private final IDocumentService documentService;
    public DocumentController(IDocumentService documentService) {
        this.documentService = documentService;
    }

    @Operation(summary="Uploads a document")
    @ApiResponses({
            @ApiResponse(responseCode="201",description="Document uploaded")
    })
    @PostMapping(consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.status(201).body(documentService.uploadFile(file));
        } catch(Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.noContent().build();
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> getDocumentById(@PathVariable String id) {
        try {
            var fileData = documentService.getDocumentFileById(id);
            var doc = documentService.getDocumentById(id);
            if(doc==null||fileData==null) {
                return ResponseEntity.notFound().build();
            }
            var headers=new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",doc.getFilename());
            return ResponseEntity.ok().headers(headers).body(fileData);
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        try {
            return ResponseEntity.ok(documentService.getAllDocuments());
        } catch(Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentDTO>> searchDocuments(@RequestParam String query) {
        try {
            if(query==null||query.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(documentService.searchDocuments(query));
        } catch(Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}