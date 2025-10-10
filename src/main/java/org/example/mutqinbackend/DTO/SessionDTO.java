package org.example.mutqinbackend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class SessionDTO {
    private String sessionId;
    private String status;
    private Instant date;
    private String sheikhId;
    private String studentUsername;
    private String sheikhUsername;
}