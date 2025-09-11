package org.example.mutqinbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "tutor_id", nullable = false)
    private User tutor;

    @NotNull
    @Column(name = "\"time\"", nullable = false)
    private Instant time;

    @Column(name = "feedback", length = Integer.MAX_VALUE)
    private String feedback;
    @Column(name = "outcome", length = Integer.MAX_VALUE)
    private String outcome;

    @Size(max = 50)
    @NotNull
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "session_url", length = Integer.MAX_VALUE)
    private String sessionUrl;


 //TODO [JPA Buddy] create field to map the 'duration' column
// Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "duration", columnDefinition = "interval(0, 0) not null")
    private Duration duration;

}