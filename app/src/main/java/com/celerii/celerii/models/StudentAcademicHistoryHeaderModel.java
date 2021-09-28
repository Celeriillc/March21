package com.celerii.celerii.models;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentAcademicHistoryHeaderModel {
    String term, year, className;
    HashMap<String, HashMap<String, ArrayList<AcademicRecordStudent>>> studentRecords;

    public StudentAcademicHistoryHeaderModel() {
        this.term = "";
        this.year = "";
        this.className = "";
        this.studentRecords = new HashMap<>();
    }

    public StudentAcademicHistoryHeaderModel(String term, String year, String className) {
        this.term = term;
        this.year = year;
        this.className = className;
        this.studentRecords = new HashMap<>();
    }

    public StudentAcademicHistoryHeaderModel(String term, String year, String className, HashMap<String, HashMap<String, ArrayList<AcademicRecordStudent>>> studentRecords) {
        this.term = term;
        this.year = year;
        this.className = className;
        this.studentRecords = studentRecords;
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

    public HashMap<String, HashMap<String, ArrayList<AcademicRecordStudent>>> getStudentRecords() {
        return studentRecords;
    }

    public void setStudentRecords(HashMap<String, HashMap<String, ArrayList<AcademicRecordStudent>>> studentRecords) {
        this.studentRecords = studentRecords;
    }
}
