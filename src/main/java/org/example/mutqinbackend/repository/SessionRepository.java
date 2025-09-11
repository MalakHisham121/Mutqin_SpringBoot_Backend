package org.example.mutqinbackend.repository;

import org.example.mutqinbackend.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByStatusAndTimeAfter(String status, Instant time);
    List<Session> findByTimeAfter(Instant time);
}