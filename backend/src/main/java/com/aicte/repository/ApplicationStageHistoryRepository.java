package com.aicte.repository;

import com.aicte.entity.ApplicationStageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationStageHistoryRepository extends JpaRepository<ApplicationStageHistory, UUID> {

    List<ApplicationStageHistory> findByApplicationIdOrderByStageEnteredAtDesc(UUID applicationId);

    List<ApplicationStageHistory> findByReminderSentFalse();
}
