package org.example.mutqinbackend.service;

import org.example.mutqinbackend.entity.CalendlyEvent;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.repository.CalendlyEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CalendlyService {

    @Value("${calendly.client-id}")
    private String clientId;

    @Value("${calendly.client-secret}")
    private String clientSecret;

    @Value("${calendly.redirect-uri}")
    private String redirectUri;

    @Value("${calendly.api-url}")
    private String calendlyApiUrl;

    private final RestTemplate restTemplate;
    private final CalendlyEventRepository calendlyEventRepository;

    public CalendlyService(RestTemplate restTemplate, CalendlyEventRepository calendlyEventRepository) {
        this.restTemplate = restTemplate;
        this.calendlyEventRepository = calendlyEventRepository;
    }

    public String getAuthorizationUrl() {
        return "https://auth.calendly.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri;
    }

    public Map<String, String> exchangeCodeForToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "authorization_code");
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("code", code);
        body.put("redirect_uri", redirectUri);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://auth.calendly.com/oauth/token",
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to exchange code for token");
    }

    public String getSchedulingUrl(String eventTypeUri, String inviteeEmail) {
        return eventTypeUri + "?email=" + inviteeEmail;
    }

    public Map<String, Object> getEventDetails(String accessToken, String eventUuid) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);


        ResponseEntity<Map> response = restTemplate.exchange(
                calendlyApiUrl + "/scheduled_events/" + eventUuid,
                HttpMethod.GET,
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to retrieve event details");
    }

    public String getSessionUrl(String accessToken, String eventUuid) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                calendlyApiUrl + "/scheduled_events/" + eventUuid,
                HttpMethod.GET,
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> event = response.getBody();
            return (String) ((Map<String, Object>) event.get("resource")).get("uri");
        }
        throw new RuntimeException("Failed to retrieve session URL");
    }

    public CalendlyEvent getCalendlyEventByTutorId(User user) {
        CalendlyEvent event = calendlyEventRepository.findFirstByUser(user);
        if (event == null) {
            throw new IllegalArgumentException("No Calendly event found for tutor");
        }
        return event;
    }

    public String getOrganizationUri(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                calendlyApiUrl + "/users/me",
                HttpMethod.GET,
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> user = (Map<String, Object>) response.getBody().get("resource");
            return (String) user.get("current_organization");
        }
        throw new RuntimeException("Failed to retrieve organization URI");
    }

    public Optional<CalendlyEvent> findFirstByEventUriContaining(String eventUri) {
        return  calendlyEventRepository.findFirstByEventUri(eventUri);
    }
}
