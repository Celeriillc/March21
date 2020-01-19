package com.celerii.celerii.models;

/**
 * Created by user on 7/23/2017.
 */

public class HistoryPerformanceBody {
    String classID, className, term, year, score, scoreNormalized, student, subject;
    String isIncrease;
    boolean isNew;

    public HistoryPerformanceBody() {
    }

    public HistoryPerformanceBody(String classID, String className, String term, String year, String score, String isIncrease) {
        this.classID = classID;
        this.className = className;
        this.term = term;
        this.year = year;
        this.score = score;
        this.isIncrease = isIncrease;
    }

    public HistoryPerformanceBody(String classID, String className, String term, String year, String score, String scoreNormalized, String student, String subject, String isIncrease) {
        this.classID = classID;
        this.className = className;
        this.term = term;
        this.year = year;
        this.score = score;
        this.scoreNormalized = scoreNormalized;
        this.student = student;
        this.subject = subject;
        this.isIncrease = isIncrease;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScoreNormalized() {
        return scoreNormalized;
    }

    public void setScoreNormalized(String scoreNormalized) {
        this.scoreNormalized = scoreNormalized;
    }

    public String getIsIncrease() {
        return isIncrease;
    }

    public void setIsIncrease(String increase) {
        this.isIncrease = increase;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
