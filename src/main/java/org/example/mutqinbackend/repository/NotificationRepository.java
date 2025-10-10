package org.example.mutqinbackend.repository;

import org.example.mutqinbackend.entity.Notification;
import org.example.mutqinbackend.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndIsReadFalse(User user);
    List<Notification> findByUser(User user);

    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.user WHERE n.id = :id")
    Notification findById(long id);

}
