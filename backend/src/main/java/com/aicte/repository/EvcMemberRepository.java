package com.aicte.repository;

import com.aicte.entity.EvcMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EvcMemberRepository extends JpaRepository<EvcMember, UUID> {
    List<EvcMember> findByEvcVisitId(UUID evcVisitId);

    @Query("SELECT m FROM EvcMember m WHERE m.user.id = :userId AND m.evcVisit.confirmedDate = :date")
    List<EvcMember> findConflictingMembers(@Param("userId") UUID userId, @Param("date") LocalDateTime date);
}
