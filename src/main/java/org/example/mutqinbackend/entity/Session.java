package org.example.mutqinbackend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @Column(name = "session_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "tutor_id", nullable = false)
    private User tutor;

    @Column(name = "\"time\"", nullable = false)
    private Instant time;

    @Column(name = "feedback", length = Integer.MAX_VALUE)
    private String feedback;
    @Column(name = "outcome", length = Integer.MAX_VALUE)
    private String outcome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getTutor() {
        return tutor;
    }

    public void setTutor(User tutor) {
        this.tutor = tutor;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

/*
 TODO [JPA Buddy] create field to map the 'duration' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "duration", columnDefinition = "interval(0, 0) not null")
    private Object duration;
*/
}