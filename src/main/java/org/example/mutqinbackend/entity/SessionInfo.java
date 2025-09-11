package org.example.mutqinbackend.entity;

import java.time.Instant;

/**
 * Projection for {@link Session}
 */
public interface SessionInfo {
    Instant getSessionTime();

    String getCalendlyUrl();

    String getFeedback();

    String getOutcome();
}