package com.aicte.repository;

import com.aicte.entity.Deadline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DeadlineRepository extends JpaRepository<Deadline, UUID> {
    List<Deadline> findByApplicationId(UUID applicationId);
    List<Deadline> findByResolvedFalseAndDueDateBefore(LocalDateTime date);
    List<Deadline> findByResolvedFalseAndEscalatedFalse();
}
