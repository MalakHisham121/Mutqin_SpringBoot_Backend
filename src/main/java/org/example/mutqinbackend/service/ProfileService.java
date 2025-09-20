package org.example.mutqinbackend.service;


import jakarta.transaction.Transactional;
import org.example.mutqinbackend.entity.Role;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.DTO.UserDto;
import org.example.mutqinbackend.exception.InvalidRoleException;
import org.example.mutqinbackend.exception.UserNotFoundException;
import org.example.mutqinbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProfileService {
    @Autowired
    private UserRepository userRepository;


    public Optional<UserDto> getUserProfilebyId(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User u = userOptional.get();

        // Determine if the current user is authorized (own profile or admin)

        // Build the DTO, including sensitive fields only if authorized
        UserDto userDTO = new UserDto(u.getId(),u.getUsername(),u.getEmail(),u.getAge(),u.getPoints(),u.getProfilePictureUrl(),u.getPhone(),u.getRole());

        return Optional.of(userDTO);
    }

    public Optional<UserDto> getSpecificRoleProfile(String role, Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID: ID must be a positive number");
        }

        Role r;
        try {
            r = Role.valueOf(role); // Convert string to Role enum
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Invalid role: " + role);
        }

        Optional<UserDto> user = userRepository.findByIdAndRole(id, String.valueOf(r)); // Query the repository
        if (user.isEmpty()) {
            throw new UserNotFoundException("No user found with ID " + id + " and role " + role);
        }

        return user;
    }
    public List<UserDto> getUsersByRole(String role) {
        Role r;
        try {
            r = Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Invalid role: " + role);
        }
        return userRepository.findByRole(String.valueOf(r));
    }

    public Optional<User> findByEmail(String inviteeEmail) {
        return userRepository.findByEmail(inviteeEmail);
    }


    // NEW: Method to update the authenticated user's profile
    public UserDto updateProfile(String email, UserDto updateDto) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        User user = userOptional.get();

        // Update fields only if provided in the DTO
        if (updateDto.getUsername() != null) {
            user.setUsername(updateDto.getUsername());
        }
        if (updateDto.getAge() != null) {
            user.setAge(updateDto.getAge());
        }
        if (updateDto.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(updateDto.getProfilePictureUrl());
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }
        // Note: Email and role are typically not updated via profile edits for security reasons
        // If needed, add additional checks (e.g., admin-only role changes)
        if (updateDto.getRole() != null) {
            // Verify if current user has admin role before updating
            Role newRole = Role.valueOf(updateDto.getRole());
            user.setRole(newRole.toString());
        }

        userRepository.save(user);
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getAge(),
                user.getPoints(), user.getProfilePictureUrl(), user.getPhone(), user.getRole());
    }

    // NEW: Method to delete the authenticated user's profile
    public void deleteProfile(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        userRepository.delete(userOptional.get());
    }
}
