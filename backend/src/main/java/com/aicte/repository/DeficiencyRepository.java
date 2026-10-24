package com.aicte.repository;

import com.aicte.entity.Deficiency;
import com.aicte.enums.DeficiencyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface DeficiencyRepository extends JpaRepository<Deficiency, UUID> {

    List<Deficiency> findByApplicationId(UUID applicationId);

    List<Deficiency> findByApplicationIdAndStatus(UUID applicationId, DeficiencyStatus status);

    long countByApplicationIdAndStatusIn(UUID applicationId, Collection<DeficiencyStatus> statuses);
}
