package com.aicte.repository;

import com.aicte.entity.Application;
import com.aicte.enums.ApplicationStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    List<Application> findByInstitutionId(UUID institutionId);

    Page<Application> findByInstitutionId(UUID institutionId, Pageable pageable);

    List<Application> findByAssignedOfficerId(UUID assignedOfficerId);

    Page<Application> findByAssignedOfficerId(UUID assignedOfficerId, Pageable pageable);

    List<Application> findByCurrentStage(ApplicationStage currentStage);

    Optional<Application> findByApplicationNumber(String applicationNumber);
}
