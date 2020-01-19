package com.celerii.celerii.models;

/**
 * Created by DELL on 10/5/2017.
 */

public class AcademicRecordStudent {
    String classID, teacherID, schoolID, studentID, recordKey;
    String className, term, academicYear, subject, date, sortableDate;
    String academicYear_Term, term_AcademicYear;
    String subject_AcademicYear_Term, subject_Term_AcademicYear, class_subject_AcademicYear_Term, class_subject_Term_AcademicYear;
    String testType, maxObtainable, percentageOfTotal, score, classAverage;
    boolean isNew;

    public AcademicRecordStudent() {
    }

//    public AcademicRecordStudent(String classID, String teacherID, String schoolID, String studentID, String term, String academicYear, String subject,
//                                 String date, String sortableDate, String academicYear_Term, String term_AcademicYear, String subject_AcademicYear_Term,
//                                 String subject_Term_AcademicYear, String class_subject_AcademicYear_Term, String class_subject_Term_AcademicYear, String testType,
//                                 String maxObtainable, String percentageOfTotal, String score) {
//        this.classID = classID;
//        this.teacherID = teacherID;
//        this.schoolID = schoolID;
//        this.studentID = studentID;
//        this.term = term;
//        this.academicYear = academicYear;
//        this.subject = subject;
//        this.date = date;
//        this.sortableDate = sortableDate;
//        this.academicYear_Term = academicYear_Term;
//        this.term_AcademicYear = term_AcademicYear;
//        this.subject_AcademicYear_Term = subject_AcademicYear_Term;
//        this.subject_Term_AcademicYear = subject_Term_AcademicYear;
//        this.class_subject_AcademicYear_Term = class_subject_AcademicYear_Term;
//        this.class_subject_Term_AcademicYear = class_subject_Term_AcademicYear;
//        this.testType = testType;
//        this.maxObtainable = maxObtainable;
//        this.percentageOfTotal = percentageOfTotal;
//        this.score = score;
//    }


    public AcademicRecordStudent(String classID, String teacherID, String schoolID, String studentID, String term, String academicYear, String subject,
                                 String date, String sortableDate, String academicYear_Term, String term_AcademicYear, String subject_AcademicYear_Term,
                                 String subject_Term_AcademicYear, String class_subject_AcademicYear_Term, String class_subject_Term_AcademicYear, String testType,
                                 String maxObtainable, String percentageOfTotal, String score, String classAverage) {
        this.classID = classID;
        this.teacherID = teacherID;
        this.schoolID = schoolID;
        this.studentID = studentID;
        this.term = term;
        this.academicYear = academicYear;
        this.subject = subject;
        this.date = date;
        this.sortableDate = sortableDate;
        this.academicYear_Term = academicYear_Term;
        this.term_AcademicYear = term_AcademicYear;
        this.subject_AcademicYear_Term = subject_AcademicYear_Term;
        this.subject_Term_AcademicYear = subject_Term_AcademicYear;
        this.class_subject_AcademicYear_Term = class_subject_AcademicYear_Term;
        this.class_subject_Term_AcademicYear = class_subject_Term_AcademicYear;
        this.testType = testType;
        this.maxObtainable = maxObtainable;
        this.percentageOfTotal = percentageOfTotal;
        this.score = score;
        this.classAverage = classAverage;
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

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getRecordKey() {
        return recordKey;
    }

    public void setRecordKey(String recordKey) {
        this.recordKey = recordKey;
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

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSortableDate() {
        return sortableDate;
    }

    public void setSortableDate(String sortableDate) {
        this.sortableDate = sortableDate;
    }

    public String getAcademicYear_Term() {
        return academicYear_Term;
    }

    public void setAcademicYear_Term(String academicYear_Term) {
        this.academicYear_Term = academicYear_Term;
    }

    public String getTerm_AcademicYear() {
        return term_AcademicYear;
    }

    public void setTerm_AcademicYear(String term_AcademicYear) {
        this.term_AcademicYear = term_AcademicYear;
    }

    public String getSubject_AcademicYear_Term() {
        return subject_AcademicYear_Term;
    }

    public void setSubject_AcademicYear_Term(String subject_AcademicYear_Term) {
        this.subject_AcademicYear_Term = subject_AcademicYear_Term;
    }

    public String getSubject_Term_AcademicYear() {
        return subject_Term_AcademicYear;
    }

    public void setSubject_Term_AcademicYear(String subject_Term_AcademicYear) {
        this.subject_Term_AcademicYear = subject_Term_AcademicYear;
    }

    public String getClass_subject_Term_AcademicYear() {
        return class_subject_Term_AcademicYear;
    }

    public void setClass_subject_Term_AcademicYear(String class_subject_Term_AcademicYear) {
        this.class_subject_Term_AcademicYear = class_subject_Term_AcademicYear;
    }

    public String getClass_subject_AcademicYear_Term() {
        return class_subject_AcademicYear_Term;
    }

    public void setClass_subject_AcademicYear_Term(String class_subject_AcademicYear_Term) {
        this.class_subject_AcademicYear_Term = class_subject_AcademicYear_Term;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getMaxObtainable() {
        return maxObtainable;
    }

    public void setMaxObtainable(String maxObtainable) {
        this.maxObtainable = maxObtainable;
    }

    public String getPercentageOfTotal() {
        return percentageOfTotal;
    }

    public void setPercentageOfTotal(String percentageOfTotal) {
        this.percentageOfTotal = percentageOfTotal;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getClassAverage() {
        return classAverage;
    }

    public void setClassAverage(String classAverage) {
        this.classAverage = classAverage;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
