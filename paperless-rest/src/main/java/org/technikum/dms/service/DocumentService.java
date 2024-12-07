package org.technikum.dms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.technikum.dms.entity.DocumentDTO;
import org.technikum.dms.mapper.DocumentMapper;
import org.technikum.dms.repository.DocumentRepository;
import org.technikum.dms.entity.Document;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentMapper documentMapper;

    public Document saveDocument(DocumentDTO documentDTO) {
        Document document = documentMapper.toEntity(documentDTO);

        document.setStatus("Uploaded");
        document.setCreatedAt(java.time.LocalDateTime.now());

        return documentRepository.save(document);
    }
}
