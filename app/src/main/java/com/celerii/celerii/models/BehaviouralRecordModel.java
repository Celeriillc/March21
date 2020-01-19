package com.celerii.celerii.models;

/**
 * Created by DELL on 5/8/2019.
 */

public class BehaviouralRecordModel {
    String classID, teacherID, studentID, recordKey;
    String term, academicYear, date, sortableDate;
    String academicYear_Term, term_AcademicYear;
    String class_academicYear_Term, class_term_AcademicYear;
    String point, rewardType, rewardDescription;
    boolean isNew;

    public BehaviouralRecordModel() {
        this.classID = "";
        this.teacherID = "";
        this.studentID = "";
        this.recordKey = "";
        this.term = "";
        this.academicYear = "";
        this.date = "";
        this.sortableDate = "";
        this.academicYear_Term = "";
        this.term_AcademicYear = "";
        this.class_academicYear_Term = "";
        this.class_term_AcademicYear = "";
        this.point = "";
        this.rewardType = "";
        this.rewardDescription = "";
        this.isNew = false;
    }

    public BehaviouralRecordModel(String classID, String teacherID, String studentID, String recordKey, String term, String academicYear, String date, String sortableDate, String academicYear_Term, String term_AcademicYear, String class_academicYear_Term, String class_term_AcademicYear, String point, String rewardType, String rewardDescription, boolean isNew) {
        this.classID = classID;
        this.teacherID = teacherID;
        this.studentID = studentID;
        this.recordKey = recordKey;
        this.term = term;
        this.academicYear = academicYear;
        this.date = date;
        this.sortableDate = sortableDate;
        this.academicYear_Term = academicYear_Term;
        this.term_AcademicYear = term_AcademicYear;
        this.class_academicYear_Term = class_academicYear_Term;
        this.class_term_AcademicYear = class_term_AcademicYear;
        this.point = point;
        this.rewardType = rewardType;
        this.rewardDescription = rewardDescription;
        this.isNew = isNew;
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

    public String getClass_academicYear_Term() {
        return class_academicYear_Term;
    }

    public void setClass_academicYear_Term(String class_academicYear_Term) {
        this.class_academicYear_Term = class_academicYear_Term;
    }

    public String getClass_term_AcademicYear() {
        return class_term_AcademicYear;
    }

    public void setClass_term_AcademicYear(String class_term_AcademicYear) {
        this.class_term_AcademicYear = class_term_AcademicYear;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public String getRewardDescription() {
        return rewardDescription;
    }

    public void setRewardDescription(String rewardDescription) {
        this.rewardDescription = rewardDescription;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
