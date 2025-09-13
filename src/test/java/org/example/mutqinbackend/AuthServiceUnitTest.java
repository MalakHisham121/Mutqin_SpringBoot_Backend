package org.example.mutqinbackend;

import org.example.mutqinbackend.DTO.LoginRequest;
import org.example.mutqinbackend.DTO.SignupRequest;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.repository.UserRepository;
import org.example.mutqinbackend.service.AuthService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceUnitTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    //dummy testcases:
    @Test
    void testSignupCreatesUserSuccessfully() {
        SignupRequest req = new SignupRequest();
        req.setUsername("validuser");
        req.setPassword("123456");
        req.setEmail("valid@example.com");
        req.setRole("USER");
        req.setAge(20);
        req.setPhone("0123456789");
        req.setMemorizationLevel("BEGINNER");

        User savedUser = authService.signup(req);

        assertNotNull(savedUser.getId(), "User should have an ID after saving");
        assertEquals("validuser", savedUser.getUsername());
        assertEquals("valid@example.com", savedUser.getEmail());
        assertEquals("USER", savedUser.getRole());
        assertEquals(0, savedUser.getPoints());
    }

    @Test
    void testSignupFailsIfUsernameExists() {
        SignupRequest req1 = new SignupRequest();
        req1.setUsername("duplicate");
        req1.setPassword("pass");
        req1.setEmail("dup@example.com");
        req1.setRole("USER");
        req1.setAge(25);
        req1.setPhone("0100000000");
        req1.setMemorizationLevel("BEGINNER");
        authService.signup(req1);

        SignupRequest req2 = new SignupRequest();
        req2.setUsername("duplicate");
        req2.setPassword("pass2");
        req2.setEmail("dup2@example.com");
        req2.setRole("USER");
        req2.setAge(30);
        req2.setPhone("0111111111");
        req2.setMemorizationLevel("BEGINNER");

        assertThrows(IllegalArgumentException.class, () -> authService.signup(req2));
    }

    @Test
    void testSignupFailsIfEmailExists() {
        SignupRequest req1 = new SignupRequest();
        req1.setUsername("user1");
        req1.setPassword("pass");
        req1.setEmail("same@example.com");
        req1.setRole("USER");
        req1.setAge(20);
        req1.setPhone("0123456789");
        req1.setMemorizationLevel("BEGINNER");
        authService.signup(req1);

        SignupRequest req2 = new SignupRequest();
        req2.setUsername("user2");
        req2.setPassword("pass2");
        req2.setEmail("same@example.com");
        req2.setRole("USER");
        req2.setAge(22);
        req2.setPhone("0198765432");
        req2.setMemorizationLevel("BEGINNER");

        assertThrows(IllegalArgumentException.class, () -> authService.signup(req2));
    }
    // testcases from SRS

    @BeforeEach
     void cleanDb() {
        userRepository.deleteAll();
    }

    private SignupRequest baseSignupRequest() {
        SignupRequest req = new SignupRequest();
        req.setUsername("validUser");
        req.setEmail("valid@example.com");
        req.setPassword("Password1!");
        req.setRole("USER");
        req.setAge(20);
        req.setPhone("01012345678");
        req.setMemorizationLevel("BEGINNER");
        return req;
    }

    // F-TC001 / F-TC002: Empty username
    @Test
    void testSignupFailsWithEmptyUsername() {
        SignupRequest req = baseSignupRequest();
        req.setUsername("");

        assertThrows(IllegalArgumentException.class, () -> authService.signup(req));
    }

    // F-TC003: Duplicate email
    @Test
    void testSignupFailsWithDuplicateEmail() {
        SignupRequest req1 = baseSignupRequest();
        req1.setUsername("user1");
        req1.setEmail("dup@example.com");
        authService.signup(req1);

        SignupRequest req2 = baseSignupRequest();
        req2.setUsername("user2");
        req2.setEmail("dup@example.com"); // duplicate

        Exception ex = assertThrows(IllegalArgumentException.class, () -> authService.signup(req2));
        assertEquals("Email already exists", ex.getMessage());
    }

    // F-TC004: Invalid age
    @Test
    void testSignupFailsWithInvalidAge() {
        SignupRequest req = baseSignupRequest();
        req.setAge(-1);

        assertThrows(IllegalArgumentException.class, () -> authService.signup(req));
    }

    // F-TC005: Invalid phone
    @Test
    void testSignupFailsWithInvalidPhone() {
        SignupRequest req = baseSignupRequest();
        req.setPhone("12345");

        assertThrows(IllegalArgumentException.class, () -> authService.signup(req));
    }

    // F-TC006 → F-TC009: Password rules
    @Test
    void testSignupFailsWithShortPassword() {
        SignupRequest req = baseSignupRequest();
        req.setPassword("pass1"); // too short

        assertThrows(IllegalArgumentException.class, () -> authService.signup(req));
    }

    @Test
    void testSignupFailsWithNoUppercase() {
        SignupRequest req = baseSignupRequest();
        req.setPassword("passwooord1");

        assertThrows(IllegalArgumentException.class, () -> authService.signup(req));
    }

    @Test
    void testSignupFailsWithNoLowercase() {
        SignupRequest req = baseSignupRequest();
        req.setPassword("PASSWORD1");

        assertThrows(IllegalArgumentException.class, () -> authService.signup(req));
    }

    @Test
    void testSignupFailsWithNoDigits() {
        SignupRequest req = baseSignupRequest();
        req.setPassword("Password"); // no digit

        assertThrows(IllegalArgumentException.class, () -> authService.signup(req));
    }

    // Valid signup (control case)
    @Test
    void testSignupSuccess() {
        SignupRequest req = baseSignupRequest();
        User saved = authService.signup(req);

        assertNotNull(saved.getId());
        assertEquals("validUser", saved.getUsername());
        assertEquals("valid@example.com", saved.getEmail());
    }

    // F-TC010: Successful login
    @Test
    void testLoginSuccess() {
        SignupRequest req = baseSignupRequest();
        req.setUsername("loginuser");
        req.setEmail("login@example.com");
        req.setPassword("Password1!");
        authService.signup(req);

        LoginRequest login = new LoginRequest();
        login.setEmail("login@example.com");
        login.setPassword("Password1!");

        String token = authService.login(login);
        assertNotNull(token);
    }

    // F-TC011: Wrong password
    @Test
    void testLoginFailsWithWrongPassword() {
        SignupRequest req = baseSignupRequest();
        req.setUsername("wrongpass");
        req.setEmail("wrong@example.com");
        req.setPassword("CorrectPass1!");
        authService.signup(req);

        LoginRequest login = new LoginRequest();
        login.setEmail("wrong@example.com");
        login.setPassword("WrongPass");

        Exception ex = assertThrows(Exception.class, () -> authService.login(login));
        assertTrue(ex.getMessage().contains("Invalid email or password"));
    }
}
