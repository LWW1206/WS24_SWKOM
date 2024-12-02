package org.technikum.dms.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
