package com.aicte.repository;

import com.aicte.entity.ComplianceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ComplianceItemRepository extends JpaRepository<ComplianceItem, UUID> {
    List<ComplianceItem> findByApplicationId(UUID applicationId);
    List<ComplianceItem> findByDueDateBeforeAndVerifiedFalse(LocalDateTime dueDate);
}
