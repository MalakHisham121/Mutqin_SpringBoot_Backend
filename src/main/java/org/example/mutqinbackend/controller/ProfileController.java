package org.example.mutqinbackend.controller;

import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.entity.UserDto;
import org.example.mutqinbackend.repository.UserRepository;
import org.example.mutqinbackend.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

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
}
