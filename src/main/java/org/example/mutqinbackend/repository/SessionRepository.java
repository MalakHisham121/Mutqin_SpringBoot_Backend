package org.example.mutqinbackend.repository;

import org.example.mutqinbackend.DTO.UserDto;
import org.example.mutqinbackend.entity.Session;
import org.example.mutqinbackend.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByStatusAndTimeAfter(String status, Instant time);
    List<Session> findByTimeAfter(Instant time);
    List<Session> findByUser(User user);
    List<Session> findByTutor(User tutor);
    List<Session> findByUserAndTutor(User user, User tutor);


    @Query("SELECT DISTINCT new org.example.mutqinbackend.DTO.UserDto(u.id, u.username, u.email, u.age, u.points, u.profilePictureUrl, u.phone, u.role) " +
            "FROM User u WHERE u IN (SELECT s.user FROM Session s WHERE s.tutor.username = :tutorUsername)")
    List<UserDto> findStudentsByTutorUsername(@Param("tutorUsername") String tutorUsername);
}