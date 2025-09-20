package org.example.mutqinbackend.DTO;

public  class ProgressStats {
    public long progressCount;
    public long totalSessionsAttended;
    public long totalPagesLearned;

    public ProgressStats(long progressCount, long totalSessionsAttended, long totalPagesLearned) {
        this.progressCount = progressCount;
        this.totalSessionsAttended = totalSessionsAttended;
        this.totalPagesLearned = totalPagesLearned;
    }
}
