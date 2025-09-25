package org.example.mutqinbackend.controller;

import jakarta.validation.Valid;
import org.example.mutqinbackend.DTO.ProgressStats;
import org.example.mutqinbackend.DTO.ProgressUpdateRequest;
import org.example.mutqinbackend.DTO.UserDto;
import org.example.mutqinbackend.entity.CalendlyEvent;
import org.example.mutqinbackend.entity.Progress;
import org.example.mutqinbackend.service.SessionService;
import org.example.mutqinbackend.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tutor/progress")
public class TutorController {

    @Autowired
    private TutorService tutorService;




    // Update student's progress (creates new record)
    @PostMapping("/{username}")
    public ResponseEntity<Progress> updateProgress(@PathVariable String username, @Valid @RequestBody ProgressUpdateRequest request) {
        Progress progress = tutorService.updateProgress(username, request);
        return new ResponseEntity<>(progress, HttpStatus.CREATED);
    }

    // Show all progress records for a student
    @GetMapping("/{username}")
    public ResponseEntity<List<Progress>> getProgress(@PathVariable String username) {
        List<Progress> progressList = tutorService.getProgress(username);
        return new ResponseEntity<>(progressList, HttpStatus.OK);
    }

    // Get total sessions attended within time and age range
    @GetMapping("/sessions")
    public ResponseEntity<Long> getTotalSessionsAttended(
            @RequestParam Instant start,
            @RequestParam Instant end,
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        long totalSessions = tutorService.getTotalSessionsAttended(start, end, minAge, maxAge);
        return new ResponseEntity<>(totalSessions, HttpStatus.OK);
    }

    // Get total pages learned within time and age range
    @GetMapping("/pages")
    public ResponseEntity<Long> getTotalPagesLearned(
            @RequestParam Instant start,
            @RequestParam Instant end,
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        long totalPages = tutorService.getTotalPagesLearned(start, end, minAge, maxAge);
        return new ResponseEntity<>(totalPages, HttpStatus.OK);
    }

    // Get stats (count, sessions, pages) within time and age range
    @GetMapping("/stats")
    public ResponseEntity<ProgressStats> getProgressStats(
            @RequestParam Instant start,
            @RequestParam Instant end,
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        long count = tutorService.countProgressInTimeAndAge(start, end, minAge, maxAge);
        long totalSessions = tutorService.getTotalSessionsAttended(start, end, minAge, maxAge);
        long totalPages = tutorService.getTotalPagesLearned(start, end, minAge, maxAge);
        ProgressStats stats = new ProgressStats(count, totalSessions, totalPages);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
    @GetMapping("/sheikhs/{tutorUsername}/students")
    public ResponseEntity<List<UserDto>> getStudentsByTutor(@PathVariable String tutorUsername) {
        List<UserDto> students = tutorService.getStudentsBySheikh(tutorUsername);
        return ResponseEntity.ok(students);
    }
    @PostMapping("/event-type-link/{tutorUsername}")
    public ResponseEntity<CalendlyEvent> addEventTypeLink(
            @PathVariable String tutorUsername,
            @RequestBody Map<String, String> body) {
        String eventTypeLink = body.get("link");
        if (eventTypeLink == null || eventTypeLink.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        CalendlyEvent event = tutorService.addEventTypeLink(tutorUsername, eventTypeLink);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }
    @GetMapping("/first-link/{tutorusername}")
    public ResponseEntity<CalendlyEvent> getAllEvents(@PathVariable String tutorusername) {
        CalendlyEvent event = tutorService.getFirstEventByTutorUsername(tutorusername);
        return ResponseEntity.ok(event);
    }



    // Basic exception handler
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}