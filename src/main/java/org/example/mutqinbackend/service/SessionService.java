package org.example.mutqinbackend.service;

import org.example.mutqinbackend.DTO.BookSessionRequest;
import org.example.mutqinbackend.DTO.AttendSessionRequest;
import org.example.mutqinbackend.DTO.SessionDTO;
import org.example.mutqinbackend.entity.Session;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.repository.SessionRepository;
import org.example.mutqinbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final CalendlyService calendlyService;

    public SessionService(SessionRepository sessionRepository, UserRepository userRepository, CalendlyService calendlyService) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.calendlyService = calendlyService;
    }

    public Map<String, String> initiateBooking(BookSessionRequest request) {
        User student = userRepository.findById(Long.valueOf(request.getStudentId()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID"));
        User tutor = userRepository.findById(request.getTutorId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid tutor ID"));

        // Assume tutor's Calendly event type URI is stored or derived
        String eventTypeUri = calendlyService.getCalendlyEventByTutorId(tutor).getEventUri();
        String schedulingUrl = calendlyService.getSchedulingUrl(eventTypeUri, student.getEmail());

        return Map.of(
                "scheduling_url", schedulingUrl,
                "message", "Redirect to Calendly to select a time slot"
        );
    }

    public Map<String, String> confirmBooking(String eventUuid, String accessToken, Long studentId, Long tutorId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID"));
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tutor ID"));

        // Retrieve event details from Calendly
        Session eventDetails = calendlyService.getEventDetails(accessToken, eventUuid);



        eventDetails.setUser(student);
        eventDetails.setTutor(tutor);

        eventDetails.setStatus("upcoming");

        eventDetails = sessionRepository.save(eventDetails);

        return Map.of(
                "session_id", eventDetails.getId().toString(),
                "message", "Session booked successfully"
        );
    }

    public Map<String, String> attendSession(AttendSessionRequest request, String accessToken) {
        Session session = sessionRepository.findById(Long.valueOf(request.getSessionId()))
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        String sessionUrl = calendlyService.getSessionUrl(accessToken, session.getSessionUrl());
        return Map.of(
                "session_url", sessionUrl,
                "message", "Session joined"
        );
    }

    public List<SessionDTO> getSessions(String status, String period) {
        Instant startTime = period != null && period.equals("week") ?
                Instant.now().minus(7, ChronoUnit.DAYS) :
                Instant.now().minus(30, ChronoUnit.DAYS);

        List<Session> sessions = status != null ?
                sessionRepository.findByStatusAndTimeAfter(status, startTime) :
                sessionRepository.findByTimeAfter(startTime);

        return sessions.stream().map(s -> {
            SessionDTO dto = new SessionDTO();
            dto.setSessionId(String.valueOf(s.getId()));
            dto.setStatus(s.getStatus());
            dto.setDate(s.getTime());
            dto.setSheikhId(String.valueOf(s.getTutor().getId()));
            return dto;
        }).collect(Collectors.toList());
    }
}