package org.example.mutqinbackend.service;

import org.example.mutqinbackend.DTO.ProgressUpdateRequest;
import org.example.mutqinbackend.DTO.UserDto;
import org.example.mutqinbackend.entity.CalendlyEvent;
import org.example.mutqinbackend.entity.Progress;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.repository.CalendlyEventRepository;
import org.example.mutqinbackend.repository.ProgressRepository;
import org.example.mutqinbackend.repository.SessionRepository;
import org.example.mutqinbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TutorService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private CalendlyEventRepository calendlyEventRepository;

    // Create a new progress record for a user
    public Progress updateProgress(String username, ProgressUpdateRequest request) {
        User user = userRepository.findByUsername(username) ;
        if(user==null) {
            new RuntimeException("User not found with username: " + username);
        }
        Progress progress = new Progress();
        progress.setUser(user);
        if(request.getPoints()!=null)
        progress.setPoints(request.getPoints());
        if(request.getMemorizationLevel()!=null)
        progress.setMemorizationLevel(request.getMemorizationLevel());
        if(request.getNumberOfSessionsAttended()!=null) {
            progress.setSessionsAttended(request.getNumberOfSessionsAttended());
        }
        if(request.getPagesLearned()!=null)
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
    public List<UserDto> getStudentsBySheikh(String sheikhUsername) {
        User sheikh = userRepository.findByUsername(sheikhUsername);
        if(sheikh ==null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sheikh not found");

        if (!"TUTOR".equals(sheikh.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must have SHEIKH role");
        }

        return sessionRepository.findStudentsByTutorUsername(sheikh.getUsername());
    }

    public CalendlyEvent addEventTypeLink(String tutorUsername, String eventTypeLink) {
        // Validate tutor
        User tutor = userRepository.findByUsername(tutorUsername);
        if (tutor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found");
        }
        if (!"TUTOR".equals(tutor.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must have SHEIKH role");
        }

        // Validate eventTypeLink
        if (eventTypeLink == null || eventTypeLink.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event type link cannot be empty");
        }

        // Check if event already exists
        Optional<CalendlyEvent> existingEvent = Optional.ofNullable(calendlyEventRepository.findFirstByUser(tutor));
        CalendlyEvent event;
        if (existingEvent.isPresent()) {
            // Update existing event
            event = existingEvent.get();
            event.setEventUri(eventTypeLink);
        } else {
            // Create new event
            event = new CalendlyEvent();
            event.setUser(tutor);
            event.setEventUri(eventTypeLink);
        }

        // Save and return
        return calendlyEventRepository.save(event);
    }
    public CalendlyEvent getFirstEventByTutorUsername(String tutorUsername) {
        // Validate tutor
        User tutor = userRepository.findByUsername(tutorUsername);
        if (tutor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found with username: " + tutorUsername);
        }
        if (!"TUTOR".equals(tutor.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must have TUTOR role");
        }

        // Fetch the first event for the tutor
        CalendlyEvent event = calendlyEventRepository.findFirstByUser(tutor);
        if (event == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No event found for tutor: " + tutorUsername);
        }

        return event;
    }
}