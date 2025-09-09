package org.example.mutqinbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.mutqinbackend.DTO.LoginRequest;
import org.example.mutqinbackend.DTO.SignupRequest;
import org.example.mutqinbackend.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            authService.signup(request);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.core.AuthenticationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.status(401).body(error);
        }
    }

    // OAuth2 user info endpoint
    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", principal.getAttribute("name"));
        userInfo.put("email", principal.getAttribute("email"));
        userInfo.put("picture", principal.getAttribute("picture"));

        return ResponseEntity.ok(userInfo);
    }

    // These endpoints couldn't be testing using postman only use brow
    @GetMapping("/oauth2/google/signup")
    public ResponseEntity<?> googleSignUp() {
        Map<String, String> response = new HashMap<>();
        response.put("follow this url in the browser", "/oauth2/authorization/google");

        return ResponseEntity.ok(response);
    }
    @GetMapping("/oauth2/google/login")
    public ResponseEntity<?> googleLogin(){
        Map<String, String> response = new HashMap<>();
        // Add a custom parameter to indicate login intent
        String authUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/oauth2/authorization/google")
                .queryParam("action", "login")
                .build().toUriString();
        response.put("follow this url in the browser", authUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/success")
    public ResponseEntity<Map<String, Object>> oauth2Success(HttpServletRequest request)  {



        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "OAuth2 authentication successful");
        response.put("token", request.getAttribute("token"));
        response.put("name", request.getAttribute("name"));
        response.put("email", request.getAttribute("email"));
        response.put("googleId", request.getAttribute("googleId"));

        return ResponseEntity.ok(response);
    }public ResponseEntity<Map<String, Object>> oauth2Success(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "googleId", required = false) String googleId) {
        Map<String, Object> response = new HashMap<>();
        if (token == null || email == null) {
            response.put("success", false);
            response.put("message", "Missing authentication details");
            return ResponseEntity.badRequest().body(response);
        }
        response.put("success", true);
        response.put("message", "OAuth2 authentication successful");
        response.put("token", token);
        response.put("name", name != null ? name : "");
        response.put("email", email);
        response.put("googleId", googleId != null ? googleId : "");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/error")
    public ResponseEntity<Map<String, Object>> oauth2Error(
            @RequestParam("error") String error,
            @RequestParam("message") String message) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        response.put("message", message);

        return ResponseEntity.badRequest().body(response);
    }

}