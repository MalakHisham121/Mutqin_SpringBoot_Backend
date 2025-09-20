package org.example.mutqinbackend.service;

import org.example.mutqinbackend.DTO.ProgressUpdateRequest;
import org.example.mutqinbackend.entity.Progress;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.repository.ProgressRepository;
import org.example.mutqinbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TutorService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgressRepository progressRepository;

    // Create a new progress record for a user
    public Progress updateProgress(String username, ProgressUpdateRequest request) {
        User user = userRepository.findByUsername(username) ;
        if(user==null) {
            new RuntimeException("User not found with username: " + username);
        }
        Progress progress = new Progress();
        progress.setUser(user);
        progress.setPoints(request.getPoints());
        progress.setMemorizationLevel(request.getMemorizationLevel());
        progress.setSessionsAttended(request.getNumberOfSessionsAttended());
        progress.setNewLearnedPages(request.getPagesLearned());
        return progressRepository.save(progress); // Triggers @PrePersist for createdAt and updatedAt
    }

    // Get all progress records for a user
    public List<Progress> getProgress(String username) {
        User user = userRepository.findByUsername(username) ;
        if(user==null) {
            new RuntimeException("User not found with username: " + username);
        }
        return progressRepository.findByUserId(user.getId());
    }

    // Sum sessions attended within a time range, filtered by age
    public long getTotalSessionsAttended(Instant start, Instant end, Integer minAge, Integer maxAge) {
        return progressRepository.sumSessionsAttendedByUpdatedAtBetweenAndUserAgeBetween(start, end, minAge, maxAge);
    }

    // Sum pages learned within a time range, filtered by age
    public long getTotalPagesLearned(Instant start, Instant end, Integer minAge, Integer maxAge) {
        return progressRepository.sumPagesLearnedByUpdatedAtBetweenAndUserAgeBetween(start, end, minAge, maxAge);
    }

    // Count progress updates within a time range, filtered by age
    public long countProgressInTimeAndAge(Instant start, Instant end, Integer minAge, Integer maxAge) {
        return progressRepository.countByUpdatedAtBetweenAndUserAgeBetween(start, end, minAge, maxAge);
    }
}