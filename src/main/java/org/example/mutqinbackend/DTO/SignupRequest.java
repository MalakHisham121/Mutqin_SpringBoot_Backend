package org.example.mutqinbackend.DTO;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank(message = "Username is required")
    @NotNull
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @NotNull
    private String email;

    @NotBlank(message = "Password is required")
    @NotNull
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    private String password;

    @NotBlank(message = "role_type is required")
    @Size(max = 20, message = "Role must not exceed 20 characters")
    @Pattern(regexp = "ADMIN|TUTOR|PARENT|STUDENT", message = "Role must be one of: ADMIN, TUTOR, PARENT, STUDENT")
    private String role;

    @Min(value = 0, message = "Age must be a positive number")
    @Max(value = 150, message = "Age must not exceed 150")
    private Integer age;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$|^$", message = "Phone number must be a valid format (e.g., +1234567890 or 123-456-7890)")
    private String phone;

    @Size(max = 50, message = "Memorization level must not exceed 50 characters")
    @Nullable
    @Pattern(regexp = "^(BEGINNER|INTERMEDIATE|ADVANCED)?$", message = "Memorization level must be one of: BEGINNER, INTERMEDIATE, ADVANCED, or null")
    private String memorizationLevel;

}