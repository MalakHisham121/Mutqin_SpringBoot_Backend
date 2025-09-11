package org.example.mutqinbackend.repository;

import jakarta.validation.constraints.NotNull;
import org.example.mutqinbackend.entity.CalendlyEvent;
import org.example.mutqinbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CalendlyEventRepository extends JpaRepository<CalendlyEvent, Long> {
    @Query("SELECT e FROM CalendlyEvent e WHERE e.user = :user ORDER BY e.id LIMIT 1")
    CalendlyEvent findFirstByUser(@NotNull @Param("user") User user);

    Optional<CalendlyEvent> findFirstByEventUri(String eventUrl);
}