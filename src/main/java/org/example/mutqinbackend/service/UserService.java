package org.example.mutqinbackend.service;

import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.entity.UserDto;
import org.example.mutqinbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String updateProfile(UserDto userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getUsername() != null) user.setUsername(userDTO.getUsername());
        if (userDTO.getPhone() != null) user.setPhone(userDTO.getPhone());

        userRepository.save(user);
        return "Profile updated successfully";
    }

    public String deleteProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
        return "Profile deleted successfully";
    }

    public UserDto getProfile(String username) {
        User user = userRepository.findByUsername(username);
        UserDto dto = new UserDto(user.getId(),user.getUsername(), user.getEmail(), user.getAge(), user.getPoints(), user.getProfilePictureUrl(), user.getPhone(), user.getRole());

        return dto;
    }

    public List<UserDto> searchProfiles(String query) {
        List<UserDto> users = userRepository.findByUsernameContainingIgnoreCase(query);
        return users.stream().map(u -> {
            UserDto dto = new UserDto(u.getId(),u.getUsername(), u.getEmail(), u.getAge(), u.getPoints(), u.getProfilePictureUrl(), u.getPhone(), u.getRole());

            return dto;
        }).collect(Collectors.toList());
    }
}