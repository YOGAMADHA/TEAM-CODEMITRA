package com.aicte.repository;

import com.aicte.entity.EvcVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvcVisitRepository extends JpaRepository<EvcVisit, UUID> {
    List<EvcVisit> findByApplicationId(UUID applicationId);
}
