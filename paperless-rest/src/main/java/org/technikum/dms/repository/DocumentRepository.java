package org.technikum.dms.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.technikum.dms.entity.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
}