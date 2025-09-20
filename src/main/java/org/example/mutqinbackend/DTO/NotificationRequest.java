package org.example.mutqinbackend.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

/**
 *
 */
@Value
public class NotificationRequest  {
    @NotNull
    String message;
    @NotNull
    String username;
}