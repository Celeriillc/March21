package com.celerii.celerii.models;

public class StudentAcademicHistoryHeaderModel {
    String term, year, className;

    public StudentAcademicHistoryHeaderModel() {
        this.term = "";
        this.year = "";
        this.className = "";
    }

    public StudentAcademicHistoryHeaderModel(String term, String year, String className) {
        this.term = term;
        this.year = year;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
