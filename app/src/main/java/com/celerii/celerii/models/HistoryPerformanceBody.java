package com.celerii.celerii.models;

/**
 * Created by user on 7/23/2017.
 */

public class HistoryPerformanceBody {
    String classID, className, term, year, score, scoreNormalized, date, student, subject;
    String isIncrease;
    Integer year_term, term_year;
    boolean isNew;

    public HistoryPerformanceBody() {
        this.classID = "";
        this.className = "";
        this.term = "";
        this.year = "";
        this.year_term = 0;
        this.term_year = 0;
        this.score = "";
        this.scoreNormalized = "";
        this.date = "0000/00/00 00:00:00:000";
        this.student = "";
        this.subject = "";
        this.isIncrease = "";
        this.isNew = false;
    }

    public HistoryPerformanceBody(String classID, String className, String term, String year, String score, String isIncrease) {
        this.classID = classID;
        this.className = className;
        this.term = term;
        this.year = year;
        this.year_term = term.equals("1") ? Integer.valueOf(year + "10") : Integer.valueOf(year + term);
        this.term_year = term.equals("1") ? Integer.valueOf("10" + year) : Integer.valueOf(term + year);
        this.score = score;
        this.scoreNormalized = "";
        this.date = "0000/00/00 00:00:00:000";
        this.student = "";
        this.subject = "";
        this.isIncrease = isIncrease;
        this.isNew = false;
    }

    public HistoryPerformanceBody(String classID, String className, String term, String year, String score, String scoreNormalized, String student, String subject, String isIncrease) {
        this.classID = classID;
        this.className = className;
        this.term = term;
        this.year = year;
        this.year_term = term.equals("1") ? Integer.valueOf(year + "10") : Integer.valueOf(year + term);
        this.term_year = term.equals("1") ? Integer.valueOf("10" + year) : Integer.valueOf(term + year);
        this.score = score;
        this.scoreNormalized = scoreNormalized;
        this.date = "0000/00/00 00:00:00:000";
        this.student = student;
        this.subject = subject;
        this.isIncrease = isIncrease;
        this.isNew = false;
    }

    public HistoryPerformanceBody(String classID, String className, String term, String year, String score, String scoreNormalized, String date, String student, String subject, String isIncrease) {
        this.classID = classID;
        this.className = className;
        this.term = term;
        this.year = year;
        this.year_term = term.equals("1") ? Integer.valueOf(year + "10") : Integer.valueOf(year + term);
        this.term_year = term.equals("1") ? Integer.valueOf("10" + year) : Integer.valueOf(term + year);
        this.score = score;
        this.scoreNormalized = scoreNormalized;
        this.date = date;
        this.student = student;
        this.subject = subject;
        this.isIncrease = isIncrease;
        this.isNew = false;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
