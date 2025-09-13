package org.example.mutqinbackend;

import org.example.mutqinbackend.DTO.SignupRequest;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DummyTest {

    @Autowired
    private AuthService authService;

    private SignupRequest baseSignupRequest(String username, String email) {
        SignupRequest req = new SignupRequest();
        req.setUsername(username);
        req.setEmail(email);
        req.setPassword("Password1!"); // no validation so accepted
        req.setRole("USER");
        req.setAge(20);                // no validation
        req.setPhone("01012345678");   // no validation
        req.setMemorizationLevel("BEGINNER");
        return req;
    }

    @Test
    void contextLoads() {
        // passes if Spring context starts correctly
        assertTrue(true);
    }

    @Test
    void signupWorksWithValidData() {
        SignupRequest req = baseSignupRequest("dummyUser", "dummy@example.com");
        User saved = authService.signup(req);

        assertNotNull(saved);                  // user object returned
        assertEquals("dummyUser", saved.getUsername());
        assertEquals("dummy@example.com", saved.getEmail());
    }

    @Test
    void signupFailsWithDuplicateEmail() {
        SignupRequest req1 = baseSignupRequest("user1", "dup@example.com");
        authService.signup(req1);

        SignupRequest req2 = baseSignupRequest("user2", "dup@example.com");
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> authService.signup(req2));

        assertEquals("Email already exists", ex.getMessage());
    }
}
