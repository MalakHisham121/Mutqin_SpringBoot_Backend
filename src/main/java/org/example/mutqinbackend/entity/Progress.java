package org.example.mutqinbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Entity
@Table(name = "progress")
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Column(name = "memorization_level", nullable = false, length = 50)
    private String memorizationLevel;

    @Column(name = "new_learned_pages")
    private Integer newLearnedPages;

    @Column(name = "revision_pages")
    private Integer revisionPages;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


    @Column(name = "sessions_attended", nullable = false)
    private Integer sessionsAttended;



    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
    public void setSessionsAttended(Integer sessionsAttended) {
        this.sessionsAttended = sessionsAttended;
    }

    public void setRevisionPages(Integer revisionPages) {
        this.revisionPages = revisionPages;
    }

    public void setNewLearnedPages(Integer newLearnedPages) {
        this.newLearnedPages = newLearnedPages;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setMemorizationLevel(String memorizationLevel) {
        this.memorizationLevel = memorizationLevel;
    }

}