package org.example.mutqinbackend.repository;

import org.example.mutqinbackend.entity.Session;
import org.example.mutqinbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByStatusAndTimeAfter(String status, Instant time);
    List<Session> findByTimeAfter(Instant time);
    List<Session> findByUser(User user);
    List<Session> findByTutor(User tutor);
    List<Session> findByUserAndTutor(User user, User tutor);
}