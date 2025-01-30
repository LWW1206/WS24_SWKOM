package org.technikum.dms.service;
import org.springframework.web.multipart.MultipartFile;
import org.technikum.dms.dto.DocumentDTO;

import java.util.List;

public interface IDocumentService {
    DocumentDTO uploadFile(MultipartFile file) throws Exception;
    void deleteDocument(String id) throws Exception;
    byte[] getDocumentFileById(String id) throws Exception;
    DocumentDTO getDocumentById(String id);
    List<DocumentDTO> getAllDocuments();
    List<DocumentDTO> searchDocuments(String query);
}
