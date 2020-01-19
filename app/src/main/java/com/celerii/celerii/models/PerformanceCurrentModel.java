package com.celerii.celerii.models;

/**
 * Created by user on 7/18/2017.
 */

public class PerformanceCurrentModel {

    String subject, classID;
    int currentScore, averageScore, classAverage;
    boolean isNew;

    public PerformanceCurrentModel() {
        this.subject = "";
        this.classID = "";
        this.currentScore = 0;
        this.averageScore = 0;
        this.classAverage = 0;
    }

    public PerformanceCurrentModel(String subject, int currentScore) {
        this.subject = subject;
        this.currentScore = currentScore;
    }

    public PerformanceCurrentModel(String subject, String classID, int currentScore, int averageScore, int classAverage) {
        this.subject = subject;
        this.classID = classID;
        this.currentScore = currentScore;
        this.averageScore = averageScore;
        this.classAverage = classAverage;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public int getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(int averageScore) {
        this.averageScore = averageScore;
    }

    public int getClassAverage() {
        return classAverage;
    }

    public void setClassAverage(int classAverage) {
        this.classAverage = classAverage;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
