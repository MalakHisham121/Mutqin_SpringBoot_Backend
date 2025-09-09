package org.example.mutqinbackend.controller;

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

    // These endpoints couldn't be testing using postman only use browser please
    @GetMapping("/oauth2/google/signup")
    public ResponseEntity<?> googleSignUp() {
        Map<String, String> response = new HashMap<>();
        response.put("url", "/oauth2/authorization/google");

        return ResponseEntity.ok(response);
    }
    @GetMapping("/oauth2/google/login")
    public ResponseEntity<?> googleLogin(){
        return null;

    }

    @GetMapping("/success")
    public ResponseEntity<Map<String, Object>> oauth2Success()  {



        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "OAuth2 authentication successful");


        // In a real application, you might want to set HTTP-only cookies here
        // response.setHeader("Set-Cookie", "jwt=" + token + "; HttpOnly; Secure; SameSite=Strict");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/error")
    public ResponseEntity<Map<String, Object>> oauth2Error(
            @RequestParam("error") String error,
            @RequestParam("message") String message) {

        logger.error("OAuth2 error endpoint called: {} - {}", error, message);

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        response.put("message", message);

        return ResponseEntity.badRequest().body(response);
    }

}