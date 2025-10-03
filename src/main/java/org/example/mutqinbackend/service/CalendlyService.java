package org.example.mutqinbackend.service;

import org.example.mutqinbackend.DTO.CalendlyResponse;
import org.example.mutqinbackend.DTO.ScheduledEvent;
import org.example.mutqinbackend.entity.CalendlyEvent;
import org.example.mutqinbackend.entity.Session;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.repository.CalendlyEventRepository;
import org.example.mutqinbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CalendlyService {

    private static final Logger logger = LoggerFactory.getLogger(CalendlyService.class);

    @Value("${calendly.client-id}")
    private String clientId;

    @Value("${calendly.client-secret}")
    private String clientSecret;

    @Value("${calendly.redirect-uri}")
    private String redirectUri;

    @Value("${calendly.api-url}")
    private String apiUrl;

    @Value("${calendly.auth-url}")
    private String authUrl;

    @Value("${calendly.token-url}")
    private String tokenUrl;
    @Value("${calendly.api.token}")
    private String calendlyAccessToken;

    private final RestTemplate restTemplate;
    private final CalendlyEventRepository calendlyEventRepository;



    public CalendlyService(RestTemplate restTemplate, CalendlyEventRepository calendlyEventRepository) {
        this.restTemplate = restTemplate;
        this.calendlyEventRepository = calendlyEventRepository;


    }

    public String getAuthorizationUrl() {
        return authUrl +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&scope=calendar:read";
    }

    public Map<String, String> exchangeCodeForToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Successfully exchanged code for token");
                return response.getBody();
            }
            throw new RuntimeException("Failed to exchange code for token: " + response.getStatusCodeValue());
        } catch (HttpClientErrorException e) {
            logger.error("Error exchanging code for token: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to exchange code: " + e.getStatusCode(), e);
        }
    }

    public String getSchedulingUrl(String eventTypeUri, String inviteeEmail) {
        return eventTypeUri + "?email=" + inviteeEmail;
    }

    public Session getEventDetails(String accessToken, String eventUuid) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(calendlyAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<CalendlyResponse<ScheduledEvent>> response = restTemplate.exchange(
                    apiUrl + "/scheduled_events/" + eventUuid,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<CalendlyResponse<ScheduledEvent>>() {}

            );

            if (response.getStatusCode() == HttpStatus.OK) {
                CalendlyResponse<ScheduledEvent> calendlyResponse = response.getBody();
                if (calendlyResponse != null && calendlyResponse.getResource() != null) {
                    ScheduledEvent event = calendlyResponse.getResource();
                    Session session = new Session();
                   // session.setCalendlyEventUri(event.getUri());
                    session.setTime(event.getStartTime().toInstant());
                    session.setStatus(event.getStatus());
                    session.setSessionUrl(event.getUri());
                    session.setDuration(Duration.between(
                            event.getStartTime().toInstant(),
                            event.getEndTime().toInstant()
                    ));
                    // TODO: Set user (student) and tutor via lookup (e.g., email or eventTypeUri)
                    logger.info("Fetched event details for UUID: {}", eventUuid);
                    return session;
                }
            }
            throw new RuntimeException("Failed to retrieve event details: " + response.getStatusCodeValue());
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching event: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to retrieve event: " + e.getStatusCode(), e);
        }
    }

    public String getSessionUrl(String accessToken, String eventUuid) {
        Session session = getEventDetails(calendlyAccessToken, eventUuid);
        return session.getSessionUrl();
    }

    public CalendlyEvent getCalendlyEventByTutorId(User user) {
        CalendlyEvent event = calendlyEventRepository.findFirstByUser(user);
        if (event == null) {
            logger.error("No Calendly event found for tutor ID: {}", user.getId());
            throw new IllegalArgumentException("No Calendly event found for tutor");
        }
        return event;
    }

    public String getOrganizationUri(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl + "/users/me",
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> user = (Map<String, Object>) response.getBody().get("resource");
                String orgUri = (String) user.get("current_organization");
                logger.info("Fetched organization URI: {}", orgUri);
                return orgUri;
            }
            throw new RuntimeException("Failed to retrieve organization URI: " + response.getStatusCodeValue());
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching organization URI: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to retrieve organization URI: " + e.getStatusCode(), e);
        }
    }

    public Optional<CalendlyEvent> findFirstByEventUriContaining(String eventUri) {
        return calendlyEventRepository.findFirstByEventUri(eventUri);
    }

    // Polling for Free plan
    public void pollSessions(String accessToken) {
        String userUri = getCurrentUserUri(accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl + "/scheduled_events?user=" + userUri + "&count=100&status=active",
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> events = (List<Map<String, Object>>) responseBody.get("collection");
                for (Map<String, Object> event : events) {
                    String eventUri = (String) event.get("uri");
                    String eventUuid = eventUri.substring(eventUri.lastIndexOf('/') + 1);
                    Session session = getEventDetails(accessToken, eventUuid);
                    // TODO: Save session to DB via SessionService
                    logger.info("Polled session: {}", eventUri);
                }
            }
        } catch (HttpClientErrorException e) {
            logger.error("Error polling sessions: {}", e.getResponseBodyAsString());
        }
    }

    private String getCurrentUserUri(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl + "/users/me",
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return (String) ((Map<String, Object>) response.getBody().get("resource")).get("uri");
            }
            throw new RuntimeException("Failed to fetch user URI: " + response.getStatusCodeValue());
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching user URI: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to fetch user URI: " + e.getStatusCode(), e);
        }
    }


}