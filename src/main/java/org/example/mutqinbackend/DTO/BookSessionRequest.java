package org.example.mutqinbackend.DTO;

import lombok.Data;

@Data
public class BookSessionRequest {
    private String studentId;
    private Long tutorId;

}
