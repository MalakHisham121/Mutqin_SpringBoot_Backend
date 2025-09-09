package org.example.mutqinbackend.service;

import jakarta.transaction.Transactional;
import org.example.mutqinbackend.DTO.LoginRequest;
import org.example.mutqinbackend.DTO.SignupRequest;
import org.example.mutqinbackend.entity.memorization_level_type;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.repository.UserRepository;
import org.example.mutqinbackend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Optional;

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

    @Transactional
    public User signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setAge(request.getAge());
        user.setPhone(request.getPhone());
        user.setMemorizationleveltype(memorization_level_type.valueOf(request.getMemorizationLevel()));
        user.setPoints(0); // Default value
        // id, googleId, provider, profilePictureUrl are not set during signup
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
}