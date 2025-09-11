package org.example.mutqinbackend.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Value
public class UserDto implements Serializable {
    Long id;
    String username;
    String email;
    Integer age;
    Integer points;
    String profilePictureUrl;
    String phone;

    @NotNull
    @Size(max = 255)
    String role;

    public UserDto(Long id, String username, String email, Integer age, Integer points, String profilePictureUrl, String phone, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.age = age;
        this.points = points;
        this.profilePictureUrl = profilePictureUrl;
        this.phone = phone;
        this.role = role;
    }
}