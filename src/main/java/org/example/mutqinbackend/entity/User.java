package org.example.mutqinbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;


    private Integer age;

    private String phone;


    @Column(name = "memorization_level")
    @Enumerated(EnumType.STRING)
    private memorization_level_type memorizationleveltype;



    @Column(name = "points")
    private Integer points;

    @Size(max = 255)
    @Column(name = "google_id")
    private String googleId;

    @Size(max = 20)
    @Column(name = "provider", length = 20)
    private String provider;

    @Column(name = "profile_picture_url", length = Integer.MAX_VALUE)
    private String profilePictureUrl;

    @Size(max = 255)
    @NotNull
    @Column(name = "role", nullable = false)
    private String role;

}

