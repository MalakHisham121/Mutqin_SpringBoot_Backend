package org.example.mutqinbackend.service;


import jakarta.transaction.Transactional;
import org.example.mutqinbackend.entity.Role;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.entity.UserDto;
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
}
