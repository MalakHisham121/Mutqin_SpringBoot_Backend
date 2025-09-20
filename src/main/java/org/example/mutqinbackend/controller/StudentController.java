package org.example.mutqinbackend.controller;

import org.example.mutqinbackend.DTO.*;
import org.example.mutqinbackend.entity.CalendlyEvent;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.DTO.UserDto;
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
        return ResponseEntity.status(200).body(sessionService.initiateBooking(request));
    }

    @PostMapping("/sessions/confirm")
    public ResponseEntity<Map<String, String>> confirmBooking(
            @RequestParam String eventUuid,
            @RequestParam Long studentId,
            @RequestParam Long tutorId,
            Authentication authentication) {
        String accessToken = "eyJraWQiOiIxY2UxZTEzNjE3ZGNmNzY2YjNjZWJjY2Y4ZGM1YmFmYThhNjVlNjg0MDIzZjdjMzJiZTgzNDliMjM4MDEzNWI0IiwidHlwIjoiSldUIiwiYWxnIjoiRVMyNTYifQ.eyJpc3MiOiJodHRwczovL2F1dGguY2FsZW5kbHkuY29tIiwiaWF0IjoxNzU4Mjk2MjQ4LCJqdGkiOiIzMTRlYzBjZi1jOGUwLTQ3MDEtODlmOS05MTA5NGVhMTAzODIiLCJ1c2VyX3V1aWQiOiI1MDljMWExMS00NmRkLTQ2OGMtOWYyNS1mYWE4NDlkMWY0ZjMiLCJhcHBfdWlkIjoiOEtEejZjRXNqWkh5MHh3dlB1ckRKaWlHNnRUb3ZINnNVc3VEOEtSTHpyQSIsImV4cCI6MTc1ODMwMzQ0OH0.G_fk2BrXDjcNbac75NuHXxQhfaWRn5K-jlKhi-ANB4eQU8lpEUGpvZKgIM8djQqUE4bdu536p5Uz3iRNX3LeCg";
        return ResponseEntity.status(201).body(sessionService.confirmBooking(eventUuid, accessToken, studentId, tutorId));
    }

    @PostMapping("/sessions/attend")
    public ResponseEntity<Map<String, String>> attendSession(@RequestBody AttendSessionRequest request, Authentication authentication) {
        String accessToken = (String) authentication.getCredentials();
        return ResponseEntity.ok(sessionService.attendSession(request, accessToken));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionDTO>> getSessions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String period) {
        return ResponseEntity.ok(sessionService.getSessions(status, period));
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody UserDto userDTO) {
        return ResponseEntity.ok(Map.of("message", userService.updateProfile(userDTO)));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Map<String, String>> deleteProfile(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(Map.of("message", userService.deleteProfile(Long.valueOf(request.get("user_id")))));
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserDto> getProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getProfile(username));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchProfiles(@RequestParam String query) {
        return ResponseEntity.ok(userService.searchProfiles(query));
    }

    @GetMapping("/calendly/auth")
    public ResponseEntity<Map<String, String>> getCalendlyAuthUrl() {
        return ResponseEntity.ok(Map.of("auth_url", calendlyService.getAuthorizationUrl()));
    }

    @GetMapping("/calendly/callback")
    public ResponseEntity<Map<String, String>> handleCalendlyCallback(@RequestParam String code) {
        Map<String, String> tokens = calendlyService.exchangeCodeForToken(code);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/calendly/webhook")
    public ResponseEntity<Map<String, String>> handleWebhook(@RequestBody Map<String, Object> webhookPayload) {
        String event = (String) webhookPayload.get("event");
        if ("invitee.created".equals(event)) {
            Map<String, Object> payload = (Map<String, Object>) webhookPayload.get("payload");
            String eventUri = (String) payload.get("event");
            String inviteeEmail = (String) ((Map<String, Object>) payload.get("invitee")).get("email");

            // Extract eventUuid from eventUri (e.g., https://api.calendly.com/scheduled_events/UUID)
            String eventUuid = eventUri.substring(eventUri.lastIndexOf('/') + 1);

            // Find student by email
            User student = profileService.findByEmail(inviteeEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found for email: " + inviteeEmail));

            // Find tutor by event URI
            CalendlyEvent calendlyEvent = calendlyService.findFirstByEventUriContaining(eventUri)
                    .orElseThrow(() -> new IllegalArgumentException("No Calendly event found for URI: " + eventUri));
            User tutor = calendlyEvent.getUser();

            // Get access token (you'll need to store it per tutor; for demo, assume it's passed or stored)
            String accessToken = "your_tutor_access_token"; // Replace with real token retrieval

            // Confirm booking
            Map<String, String> result = sessionService.confirmBooking(eventUuid, accessToken, student.getId(), tutor.getId());
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.ok(Map.of("message", "Webhook received but not processed"));
    }
}