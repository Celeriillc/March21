package com.celerii.celerii.adapters;

/**
 * Created by DELL on 8/20/2017.
 */

public class TeacherPerformanceRowMain {
    String className, classID, teacherID, subject, term, year, score;
    Integer year_term, term_year;
    String isIncrease;

    public TeacherPerformanceRowMain() {
        this.className = "";
        this.classID = "";
        this.teacherID = "";
        this.subject = "";
        this.term = "";
        this.year = "";
        this.year_term = 0;
        this.term_year = 0;
        this.score = "";
        this.isIncrease = "neutral";
    }

    public TeacherPerformanceRowMain(String className, String classID, String teacherID, String subject, String term, String year, String score, String isIncrease) {
        this.className = className;
        this.classID = classID;
        this.teacherID = teacherID;
        this.subject = subject;
        this.term = term;
        this.year = year;
        this.year_term = term.equals("1") ? Integer.valueOf(year + "10") : Integer.valueOf(year + term);
        this.term_year = term.equals("1") ? Integer.valueOf("10" + year) : Integer.valueOf(term + year);
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

    public Integer getYear_term() {
        return year_term;
    }

    public void setYear_term(Integer year_term) {
        this.year_term = year_term;
    }

    public Integer getTerm_year() {
        return term_year;
    }

    public void setTerm_year(Integer term_year) {
        this.term_year = term_year;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getIncrease() {
        return isIncrease;
    }

    public void setIncrease(String increase) {
        isIncrease = increase;
    }
}
