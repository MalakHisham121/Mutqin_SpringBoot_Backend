package org.example.mutqinbackend.controller;

import org.example.mutqinbackend.DTO.*;
import org.example.mutqinbackend.entity.CalendlyEvent;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.service.CalendlyService;
import org.example.mutqinbackend.service.ProfileService;
import org.example.mutqinbackend.service.SessionService;
import org.example.mutqinbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final SessionService sessionService;
    private final UserService userService;
    private final CalendlyService calendlyService;
    private final ProfileService profileService;

    public StudentController(SessionService sessionService, UserService userService, CalendlyService calendlyService, ProfileService profileService) {
        this.sessionService = sessionService;
        this.userService = userService;
        this.calendlyService = calendlyService;
        this.profileService = profileService;
    }

    @PostMapping("/sessions/book")
    public ResponseEntity<Map<String, String>> initiateBooking(@RequestBody BookSessionRequest request) {
        return ResponseEntity.ok(sessionService.initiateBooking(request));
    }

    @PostMapping("/sessions/confirm")
    public ResponseEntity<Map<String, String>> confirmBooking(
            @RequestParam String eventUuid,
            @RequestParam Long studentId,
            @RequestParam Long tutorId,
            Authentication authentication) {
        String accessToken = ""; // TODO: Replace with DB lookup
        return ResponseEntity.status(201).body(sessionService.confirmBooking(eventUuid, accessToken, studentId, tutorId));
    }

    @PostMapping("/sessions/attend")
    public ResponseEntity<Map<String, String>> attendSession(@RequestBody AttendSessionRequest request, Authentication authentication) {
        String accessToken = "";
        return ResponseEntity.ok(sessionService.attendSession(request, accessToken));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionDTO>> getSessions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String period) {
        return ResponseEntity.ok(sessionService.getSessions(status, period));
    }


    @GetMapping("/profile/{username}")
    public ResponseEntity<UserDto> getProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getProfile(username));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchProfiles(@RequestParam String query) {
        return ResponseEntity.ok(userService.searchProfiles(query));
    }



    @GetMapping("/calendly/callback")
    public ResponseEntity<Map<String, String>> handleCalendlyCallback(@RequestParam String code) {
        Map<String, String> tokens = calendlyService.exchangeCodeForToken(code);

        return ResponseEntity.ok(tokens);
    }
}