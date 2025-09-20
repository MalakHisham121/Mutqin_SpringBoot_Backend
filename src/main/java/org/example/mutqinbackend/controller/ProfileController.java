package org.example.mutqinbackend.controller;

import org.example.mutqinbackend.DTO.MyProfileDTO;
import org.example.mutqinbackend.DTO.UserDto;
import org.example.mutqinbackend.exception.UserNotFoundException;
import org.example.mutqinbackend.service.ProfileService;
import org.example.mutqinbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<Optional<UserDto>> getProfile(@RequestParam long id) {
        Optional<UserDto> user = profileService.getUserProfilebyId(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/role")
    public ResponseEntity<Optional<UserDto>> getSpecificRoleProfile(@RequestParam String role, @RequestParam Long id) {
        Optional<UserDto> user = profileService.getSpecificRoleProfile(role, id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<UserDto>> getUsersByRole(@RequestParam String role) {
        List<UserDto> users = profileService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/user")
    public ResponseEntity<?> getUser() {
        try {
            // Get user ID from SecurityContext (assuming JWT sets user ID as principal or name)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName(); // Assumes user ID is set as the principal's name

            // Fetch user from database
            Optional<MyProfileDTO> user = userService.findbyEmail(email);
            if (user.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error");
        }
    }
    @PutMapping("")
    public ResponseEntity<?> updateProfile(@RequestBody UserDto updateDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName(); // Get email from JWT token
            UserDto updatedUser = profileService.updateProfile(email, updateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    // NEW: Endpoint to delete the authenticated user's profile
    @DeleteMapping("")
    public ResponseEntity<?> deleteProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName(); // Get email from JWT token
            profileService.deleteProfile(email);
            return ResponseEntity.ok("Profile deleted successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

}
