package org.technikum.dms.repository;

import org.technikum.dms.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DocumentRepository extends JpaRepository<Document, String> {
}