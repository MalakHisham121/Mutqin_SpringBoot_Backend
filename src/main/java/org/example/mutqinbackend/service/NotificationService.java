package org.example.mutqinbackend.service;

import org.example.mutqinbackend.entity.Notification;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.exception.ResourceNotFoundException;
import org.example.mutqinbackend.repository.NotificationRepository;
import org.example.mutqinbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new notification for a user by username
    public Notification createNotification(String message, String username) {
        User user = userRepository.findByUsername(username) ;
        if(user==null){
            throw new RuntimeException("Userame not found");
        }
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUser(user);
        notification.setIsRead(false); // Explicitly set default value
        notification.setCreatedAt(Instant.now()); // Set creation timestamp
        return notificationRepository.save(notification);
    }

    // Get unread notifications for a specific user
    public List<Notification> getUnreadNotifications(Long userid) {
        Optional<User> user = userRepository.findById(userid);
        if(user.isEmpty()){
            throw new ResourceNotFoundException("User not found with ID: " + userid);
        }
        User user1 = user.get();

        return notificationRepository.findByUserAndIsReadFalse(user1);
    }

    // Get all notifications for a specific user
    public List<Notification> getAllNotifications(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        User user1 = user.get();
        return notificationRepository.findByUser(user1);
    }

    // Mark a notification as read by its ID
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
                notificationRepository.save(notification);
                return notification;
    }
}