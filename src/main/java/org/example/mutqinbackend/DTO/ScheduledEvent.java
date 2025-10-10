package org.example.mutqinbackend.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

public class ScheduledEvent {
    private String uri;
    private String name;
    private String status;

    @JsonProperty("start_time")
    private ZonedDateTime startTime;

    @JsonProperty("end_time")
    private ZonedDateTime endTime;

    @JsonProperty("event_type")
    private String eventTypeUri;

    // Getters and Setters
    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public ZonedDateTime getStartTime() { return startTime; }
    public void setStartTime(ZonedDateTime startTime) { this.startTime = startTime; }
    public ZonedDateTime getEndTime() { return endTime; }
    public void setEndTime(ZonedDateTime endTime) { this.endTime = endTime; }
    public String getEventTypeUri() { return eventTypeUri; }
    public void setEventTypeUri(String eventTypeUri) { this.eventTypeUri = eventTypeUri; }
}

