package com.aicte.repository;

import com.aicte.entity.DocumentRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRequirementRepository extends JpaRepository<DocumentRequirement, UUID> {

    List<DocumentRequirement> findByApplicationCategoryAndActiveTrue(String applicationCategory);
}
