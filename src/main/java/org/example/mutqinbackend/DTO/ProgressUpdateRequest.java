package org.example.mutqinbackend.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ProgressUpdateRequest {

    Integer points;


    String memorizationLevel;


    Integer numberOfSessionsAttended;
    Integer pagesLearned;
}

