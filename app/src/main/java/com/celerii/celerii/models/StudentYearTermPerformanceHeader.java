package com.celerii.celerii.models;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentYearTermPerformanceHeader {
    String subject, studentID;
    HashMap<String, ArrayList<AcademicRecordStudent>> subjectRecord;

    public StudentYearTermPerformanceHeader() {
        this.subject = "";
        this.studentID = "";
        this.subjectRecord = new HashMap<String, ArrayList<AcademicRecordStudent>>();
    }

    public StudentYearTermPerformanceHeader(String studentID, HashMap<String, ArrayList<AcademicRecordStudent>> subjectRecord) {
        this.subject = "";
        this.studentID = studentID;
        this.subjectRecord = subjectRecord;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public HashMap<String, ArrayList<AcademicRecordStudent>> getSubjectRecord() {
        return subjectRecord;
    }

    public void setSubjectRecord(HashMap<String, ArrayList<AcademicRecordStudent>> subjectRecord) {
        this.subjectRecord = subjectRecord;
    }
}
