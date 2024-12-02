package org.technikum.dms.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping
    public ResponseEntity<String> uploadDocument(@RequestBody DocumentDTO documentDTO) {
        Document savedDocument = documentService.saveDocument(documentDTO);

        return ResponseEntity.ok("Document uploaded successfully with ID: " + savedDocument.getId());
    }
}
