package com.aicte.repository;

import com.aicte.entity.Undertaking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UndertakingRepository extends JpaRepository<Undertaking, UUID> {
    List<Undertaking> findByApplicationId(UUID applicationId);
}
