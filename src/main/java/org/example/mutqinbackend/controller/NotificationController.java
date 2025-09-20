package org.example.mutqinbackend.controller;

import jakarta.validation.Valid;
import org.example.mutqinbackend.DTO.NotificationRequest;
import org.example.mutqinbackend.entity.Notification;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Create a new notification
    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody NotificationRequest request) {
        Notification notification = notificationService.createNotification(request.getMessage(), request.getUsername());
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    // Get all unread notifications for a user
    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    // Get all notifications for a user
    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getAllNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getAllNotifications(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    // Mark a notification as read
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long notificationId) {
        Notification notification = notificationService.markAsRead(notificationId);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    // Basic exception handler for not found
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
}