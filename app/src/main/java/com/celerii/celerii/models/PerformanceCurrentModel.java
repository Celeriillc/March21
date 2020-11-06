package com.celerii.celerii.models;

/**
 * Created by user on 7/18/2017.
 */

public class PerformanceCurrentModel {

    String subject, classID, date;
    int currentScore, averageScore, classAverage;
    boolean isNew;

    public PerformanceCurrentModel() {
        this.subject = "";
        this.classID = "";
        this.date = "0000/00/00 00:00:00:000";
        this.currentScore = 0;
        this.averageScore = 0;
        this.classAverage = 0;
        this.isNew = false;
    }

    public PerformanceCurrentModel(String subject, int currentScore) {
        this.subject = subject;
        this.classID = "";
        this.date = "0000/00/00 00:00:00:000";
        this.currentScore = currentScore;
        this.averageScore = 0;
        this.classAverage = 0;
        this.isNew = false;
    }

    public PerformanceCurrentModel(String subject, String date, int currentScore) {
        this.subject = subject;
        this.classID = "";
        this.date = date;
        this.currentScore = currentScore;
        this.averageScore = 0;
        this.classAverage = 0;
        this.isNew = false;
    }

    public PerformanceCurrentModel(String subject, String classID, int currentScore, int averageScore, int classAverage) {
        this.subject = subject;
        this.classID = classID;
        this.date = "0000/00/00 00:00:00:000";
        this.currentScore = currentScore;
        this.averageScore = averageScore;
        this.classAverage = classAverage;
        this.isNew = false;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
