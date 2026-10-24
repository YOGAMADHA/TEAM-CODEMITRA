package com.aicte.repository;

import com.aicte.entity.Escalation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EscalationRepository extends JpaRepository<Escalation, UUID> {
    List<Escalation> findByResolvedFalse();
    List<Escalation> findByApplicationId(UUID applicationId);
    List<Escalation> findByEscalatedToIdAndResolvedFalse(UUID userId);
}
