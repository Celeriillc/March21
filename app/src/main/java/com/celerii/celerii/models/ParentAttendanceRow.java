package com.celerii.celerii.models;

/**
 * Created by user on 7/15/2017.
 */

public class ParentAttendanceRow {
    String date, attendanceStatus, term, day, month, year, remark, subject, key;
    String studentID, classID, className, schoolID, teacherID, device;
    Boolean isNew;

    public ParentAttendanceRow() {
        this.date = "";
        this.attendanceStatus = "";
        this.term = "";
        this.day = "";
        this.month = "";
        this.year = "";
        this.remark = "";
        this.subject = "";
        this.key = "";
        this.studentID = "";
        this.classID = "";
        this.className = "";
        this.schoolID = "";
        this.teacherID = "";
        this.device = "";
        this.isNew = false;
    }

    public ParentAttendanceRow(String date, String attendanceStatus, String term, String day, String month, String year, String remark, String classID, String schoolID, String teacherID, String device) {
        this.date = date;
        this.attendanceStatus = attendanceStatus;
        this.term = term;
        this.day = day;
        this.month = month;
        this.year = year;
        this.remark = remark;
        this.subject = "";
        this.key = "";
        this.studentID = "";
        this.classID = classID;
        this.className = "";
        this.schoolID = schoolID;
        this.teacherID = teacherID;
        this.device = device;
        this.isNew = false;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
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

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }
}
