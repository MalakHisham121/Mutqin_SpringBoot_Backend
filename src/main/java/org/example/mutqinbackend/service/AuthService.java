package org.example.mutqinbackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.example.mutqinbackend.DTO.LoginRequest;
import org.example.mutqinbackend.DTO.SignupRequest;
import org.example.mutqinbackend.entity.memorization_level_type;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.repository.UserRepository;
import org.example.mutqinbackend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService2 userDetailsService;
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.app.reset-token.validity-minutes}")
    private int tokenValidityMinutes;

    @Value("${spring.app.base-url}")
    private String baseUrl;

    @Transactional
    public User signup(@Valid SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        String[] validRoles = {"STUDENT", "TUTOR", "ADMIN", "PARENT"};
        if (!Arrays.asList(validRoles).contains(request.getRole())) {
            throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }
        try {
            if(request.getMemorizationLevel()!=null)
            memorization_level_type.valueOf(request.getMemorizationLevel());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid memorization level: " + request.getMemorizationLevel());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setAge(request.getAge());
        user.setPhone(request.getPhone());
        if(request.getMemorizationLevel()!=null) {
            user.setMemorizationleveltype(memorization_level_type.valueOf(request.getMemorizationLevel()));
        }
        user.setPoints(0); // Default value
        try {

            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        }
    }

    public String login(LoginRequest request) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtTokenProvider.generateToken(authentication);

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password", e);
        }
    }


    public String loginWithOAuth2(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub");

        if (email == null) {
            throw new IllegalArgumentException("Email not found in OAuth2 user info");
        }

        // Find or create user
        User user = findOrCreateOAuth2User(email, name, googleId);

        // Create authentication object for JWT generation
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        // Generate and return JWT token
        return jwtTokenProvider.generateToken(authentication);
    }


    private User findOrCreateOAuth2User(String email, String name, String googleId) {
        Optional<User> user = userRepository.findByEmail(email);
         User user1 = user.get();
        if (user1!=null) {
            // Update Google ID if not set
            if (user1.getGoogleId() == null) {
                user1.setGoogleId(googleId);
                userRepository.save(user1);
            }
            return user1;
        }

        // Create new user for OAuth2
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(generateUniqueUsername(email, name));
        newUser.setGoogleId(googleId);
        newUser.setRole("STUDENT"); // Default role
        newUser.setPoints(0);


        return userRepository.save(newUser);
    }

    private String generateUniqueUsername(String email, String name) {
        String baseUsername = name != null ? name.replaceAll("\\s+", "").toLowerCase()
                : email.split("@")[0];

        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }


    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    @Transactional
    public void initiatePasswordReset(String email) throws MessagingException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("No user found with email: " + email);
        }

        User user = userOpt.get();
        // Generate JWT reset token with custom claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("purpose", "password_reset");
        String token = jwtTokenProvider.generateTokenForOAuth2User(user.getEmail()); // Expiry in ms
        userRepository.save(user);

        // Send email with reset link
        String resetLink = baseUrl + "/api/auth/reset-password?token=" + token;
        sendResetEmail(user.getEmail(), resetLink);
    }

    private void sendResetEmail(String toEmail, String resetLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject("Password Reset Request");
        helper.setText(
                "<h1>Reset Your Password</h1>" +
                        "<p>Click the link below to reset your password. This link expires in " + tokenValidityMinutes + " minutes.</p>" +
                        "<a href=\"" + resetLink + "\">Reset Password</a>",
                true // HTML
        );
        mailSender.send(message);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Validate JWT token
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        String email = jwtTokenProvider.getUsernameFromToken(token); // Assumes email is stored as subject
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("No user found for token");
        }

        User user = userOpt.get();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}