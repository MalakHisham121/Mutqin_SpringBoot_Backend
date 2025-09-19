package org.example.mutqinbackend.repository;

import org.example.mutqinbackend.entity.MyProfileDTO;
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
    @Query("SELECT new org.example.mutqinbackend.entity.MyProfileDTO(u.username, u.email, u.password, u.age, u.phone, u.memorizationleveltype, u.points, u.profilePictureUrl, u.role) " +
            "FROM User u WHERE u.email = :email")
    Optional<MyProfileDTO> findByEmaill(@Param("email") String email);
    User findByGoogleId(String googleId);

    Optional<User> findByEmail(@Param("email") String email);
    List<UserDto> findByUsernameContainingIgnoreCase(String username);
    @Query("SELECT new org.example.mutqinbackend.entity.UserDto(u.id, u.username, u.email,u.age,u.points, u.profilePictureUrl,u.phone, u.role) " +
            "FROM User u WHERE u.id = :id AND u.role = :role")
    Optional<UserDto> findByIdAndRole(@Param("id") Long id, @Param("role") String role);

    @Query("SELECT new org.example.mutqinbackend.entity.UserDto(u.id, u.username, u.email,u.age,u.points, u.profilePictureUrl,u.phone, u.role)" +
            "FROM User u WHERE u.role = :role")
    List<UserDto> findByRole(@Param("role") String role);
}
