package com.celerii.celerii.models;

/**
 * Created by DELL on 4/26/2019.
 */

public class PerformanceCurrentHeader {
    String term, year, termAverage, classAverage, maxPossibleAverage, className, school, student, errorMessage;

    public PerformanceCurrentHeader() {
        this.term = "";
        this.year = "";
        this.termAverage = "";
        this.classAverage = "";
        this.maxPossibleAverage = "";
        this.className = "";
        this.school = "";
        this.student = "";
        this.errorMessage = "";
    }

    public PerformanceCurrentHeader(String term, String year, String termAverage, String classAverage, String maxPossibleAverage, String className, String school, String student) {
        this.term = term;
        this.year = year;
        this.termAverage = termAverage;
        this.classAverage = classAverage;
        this.maxPossibleAverage = maxPossibleAverage;
        this.className = className;
        this.school = school;
        this.student = student;
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

    public String getTermAverage() {
        return termAverage;
    }

    public void setTermAverage(String termAverage) {
        this.termAverage = termAverage;
    }

    public String getClassAverage() {
        return classAverage;
    }

    public void setClassAverage(String classAverage) {
        this.classAverage = classAverage;
    }

    public String getMaxPossibleAverage() {
        return maxPossibleAverage;
    }

    public void setMaxPossibleAverage(String maxPossibleAverage) {
        this.maxPossibleAverage = maxPossibleAverage;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}