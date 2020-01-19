package com.celerii.celerii.models;

/**
 * Created by user on 7/18/2017.
 */

public class PerformanceHistoryModel {
    String subject;
    int averageScore;
    boolean isNew;

    public PerformanceHistoryModel() {
    }

    public PerformanceHistoryModel(String subject, int averageScore) {
        this.subject = subject;
        this.averageScore = averageScore;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(int averageScore) {
        this.averageScore = averageScore;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
