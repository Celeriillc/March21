package com.celerii.celerii.models;

/**
 * Created by DELL on 8/19/2017.
 */

public class TeacherPerformanceRow {
    String previousScore, currentScore, projectedScore;
    String subject, subject_year_term;
    boolean isWithValue = true;

    public TeacherPerformanceRow() {
        this.previousScore = "";
        this.currentScore = "";
        this.projectedScore = "";
        this.subject = "";
        this.subject_year_term = "";
    }

    public TeacherPerformanceRow(String previousScore, String currentScore, String projectedScore, String subject, String subject_year_term) {
        this.previousScore = previousScore;
        this.currentScore = currentScore;
        this.projectedScore = projectedScore;
        this.subject = subject;
        this.subject_year_term = subject_year_term;
    }

    public String getPreviousScore() {
        return previousScore;
    }

    public void setPreviousScore(String previousScore) {
        this.previousScore = previousScore;
    }

    public String getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(String currentScore) {
        this.currentScore = currentScore;
    }

    public String getProjectedScore() {
        return projectedScore;
    }

    public void setProjectedScore(String projectedScore) {
        this.projectedScore = projectedScore;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject_year_term() {
        return subject_year_term;
    }

    public void setSubject_year_term(String subject_year_term) {
        this.subject_year_term = subject_year_term;
    }

    public boolean isWithValue() {
        return isWithValue;
    }

    public void setWithValue(boolean withValue) {
        isWithValue = withValue;
    }
}
