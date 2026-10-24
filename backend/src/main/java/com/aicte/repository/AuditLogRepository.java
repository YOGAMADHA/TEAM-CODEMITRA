package com.aicte.repository;

import com.aicte.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByEntityTypeAndEntityIdOrderByPerformedAtDesc(String entityType, UUID entityId);

    Page<AuditLog> findAllByOrderByPerformedAtDesc(Pageable pageable);

    List<AuditLog> findAllByOrderByPerformedAtAsc();

    Optional<AuditLog> findTopByOrderByPerformedAtDesc();

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:entityType IS NULL OR a.entityType = :entityType) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:performedBy IS NULL OR a.performedBy = :performedBy) AND " +
           "(:from IS NULL OR a.performedAt >= :from) AND " +
           "(:to IS NULL OR a.performedAt <= :to) " +
           "ORDER BY a.performedAt DESC")
    Page<AuditLog> searchAuditLogs(
            @Param("entityType") String entityType,
            @Param("action") String action,
            @Param("performedBy") String performedBy,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:entityType IS NULL OR a.entityType = :entityType) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:performedBy IS NULL OR a.performedBy = :performedBy) AND " +
           "(:from IS NULL OR a.performedAt >= :from) AND " +
           "(:to IS NULL OR a.performedAt <= :to) " +
           "ORDER BY a.performedAt DESC")
    List<AuditLog> searchAuditLogsUnpaged(
            @Param("entityType") String entityType,
            @Param("action") String action,
            @Param("performedBy") String performedBy,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
