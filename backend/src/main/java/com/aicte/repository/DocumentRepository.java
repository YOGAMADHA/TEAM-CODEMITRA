package com.aicte.repository;

import com.aicte.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findByApplicationId(UUID applicationId);

    List<Document> findByApplicationIdAndDeletedFalse(UUID applicationId);

    List<Document> findByApplicationIdAndDocumentRequirementIdAndDeletedFalse(UUID applicationId, UUID documentRequirementId);

    Optional<Document> findByChecksum(String checksum);
}
