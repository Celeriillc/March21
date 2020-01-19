package com.celerii.celerii.adapters;

/**
 * Created by DELL on 8/20/2017.
 */

public class TeacherPerformanceRowMain {
    String className, classID, teacherID, subject, term, year, score;
    boolean isIncrease;

    public TeacherPerformanceRowMain() {
        this.className = "";
        this.term = "";
        this.year = "";
        this.score = "";
        this.isIncrease = false;
    }

    public TeacherPerformanceRowMain(String className, String classID, String teacherID, String subject, String term, String year, String score, boolean isIncrease) {
        this.className = className;
        this.classID = classID;
        this.teacherID = teacherID;
        this.subject = subject;
        this.term = term;
        this.year = year;
        this.score = score;
        this.isIncrease = isIncrease;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public boolean isIncrease() {
        return isIncrease;
    }

    public void setIncrease(boolean increase) {
        isIncrease = increase;
    }
}
