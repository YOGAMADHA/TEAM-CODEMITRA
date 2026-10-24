package com.aicte.repository;

import com.aicte.entity.AiAssistance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AiAssistanceRepository extends JpaRepository<AiAssistance, UUID> {
    List<AiAssistance> findByApplicationIdOrderByRequestedAtDesc(UUID applicationId);
    List<AiAssistance> findByDocumentIdOrderByRequestedAtDesc(UUID documentId);
    Page<AiAssistance> findByHumanConfirmedFalseOrderByRequestedAtDesc(Pageable pageable);
}
