package org.example.mutqinbackend.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.entity.memorization_level_type;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Value
public class MyProfileDTO implements Serializable {
    String username;
    String email;
    String password;
    Integer age;
    String phone;
    memorization_level_type memorizationleveltype;
    Integer points;
    String profilePictureUrl;
    @NotNull
    @Size(max = 255)
    String role;
}