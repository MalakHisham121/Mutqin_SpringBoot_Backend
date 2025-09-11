package org.example.mutqinbackend.repository;

import org.example.mutqinbackend.entity.User;
import org.example.mutqinbackend.entity.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    User findByGoogleId(String googleId);
    List<UserDto> findByUsernameContainingIgnoreCase(String username);
    @Query("SELECT new org.example.mutqinbackend.entity.UserDto(u.id, u.username, u.email,u.age,u.points, u.profilePictureUrl,u.phone, u.role) " +
            "FROM User u WHERE u.id = :id AND u.role = :role")
    Optional<UserDto> findByIdAndRole(@Param("id") Long id, @Param("role") String role);

    @Query("SELECT new org.example.mutqinbackend.entity.UserDto(u.id, u.username, u.email,u.age,u.points, u.profilePictureUrl,u.phone, u.role)" +
            "FROM User u WHERE u.role = :role")
    List<UserDto> findByRole(@Param("role") String role);
}
