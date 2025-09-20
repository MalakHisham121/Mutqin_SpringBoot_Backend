package org.example.mutqinbackend.repository;

import org.example.mutqinbackend.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(p.sessionsAttended), 0) FROM Progress p JOIN p.user u WHERE p.updatedAt BETWEEN :start AND :end AND u.age BETWEEN :minAge AND :maxAge")
    long sumSessionsAttendedByUpdatedAtBetweenAndUserAgeBetween(@Param("start") Instant start, @Param("end") Instant end, @Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

    @Query("SELECT COALESCE(SUM(p.newLearnedPages),sum(p.revisionPages), 0) FROM Progress p JOIN p.user u WHERE p.updatedAt BETWEEN :start AND :end AND u.age BETWEEN :minAge AND :maxAge")
    long sumPagesLearnedByUpdatedAtBetweenAndUserAgeBetween(@Param("start") Instant start, @Param("end") Instant end, @Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

    @Query("SELECT COUNT(p) FROM Progress p JOIN p.user u WHERE p.updatedAt BETWEEN :start AND :end AND u.age BETWEEN :minAge AND :maxAge")
    long countByUpdatedAtBetweenAndUserAgeBetween(@Param("start") Instant start, @Param("end") Instant end, @Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
}