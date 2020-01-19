package com.celerii.celerii.models;

/**
 * Created by DELL on 8/15/2017.
 */

public class TeacherAttendanceRow {
    String name, attendanceStatus, imageURL, date, sortableDate, term, day, month, year, remark, month_year, term_year, key;
    String studentID, schoolID, classID, subject, teacherID, subject_term_year, year_month_day;

    public TeacherAttendanceRow() {
    }

    public TeacherAttendanceRow(String name, String attendanceStatus, String imageURL, String date, String term, String day, String month, String year, String remark, String month_year, String term_year) {
        this.name = name;
        this.attendanceStatus = attendanceStatus;
        this.imageURL = imageURL;
        this.date = date;
        this.term = term;
        this.day = day;
        this.month = month;
        this.year = year;
        this.remark = remark;
        this.month_year = month_year;
        this.term_year = term_year;
    }

    public TeacherAttendanceRow(String name, String studentID, String attendanceStatus, String remark, String imageURL) {
        this.name = name;
        this.studentID = studentID;
        this.attendanceStatus = attendanceStatus;
        this.remark = remark;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMonth_year() {
        return month_year;
    }

    public void setMonth_year(String month_year) {
        this.month_year = month_year;
    }

    public String getTerm_year() {
        return term_year;
    }

    public void setTerm_year(String term_year) {
        this.term_year = term_year;
    }

    public String getSubject_term_year() {
        return subject_term_year;
    }

    public void setSubject_term_year(String subject_term_year) {
        this.subject_term_year = subject_term_year;
    }

    public String getYear_month_day() {
        return year_month_day;
    }

    public void setYear_month_day(String year_month_day) {
        this.year_month_day = year_month_day;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
