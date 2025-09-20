package org.example.mutqinbackend.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ProgressUpdateRequest {
    @NotNull
    Integer points;

    @NotNull
    String memorizationLevel;

    @NotNull
    Integer numberOfSessionsAttended;

    @NotNull
    Integer pagesLearned;
}

