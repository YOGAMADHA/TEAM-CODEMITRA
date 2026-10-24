package com.aicte.repository;

import com.aicte.entity.ApplicationAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationAssignmentRepository extends JpaRepository<ApplicationAssignment, UUID> {

    Optional<ApplicationAssignment> findByApplicationIdAndActiveTrue(UUID applicationId);
}
