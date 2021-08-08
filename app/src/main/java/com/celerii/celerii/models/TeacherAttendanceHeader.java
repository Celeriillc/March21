package com.celerii.celerii.models;

/**
 * Created by DELL on 8/17/2017.
 */

public class TeacherAttendanceHeader {
    String className, subject, term, date, teacher, noOfStudents, noOfBoys, noOfGirls, key;
    String day, month, year, sortableDate, teacherID, classID, schoolID, device;
    String month_year, term_year, subject_term_year, year_month_day;
    String present, absent, late;

    public TeacherAttendanceHeader() {
        this.className = "";
        this.subject = "";
        this.term = "";
        this.date = "";
        this.teacher = "";
        this.noOfStudents = "";
        this.noOfBoys = "";
        this.noOfGirls = "";
        this.key = "";
        this.day = "";
        this.month = "";
        this.year = "";
        this.sortableDate = "";
        this.teacherID = "";
        this.classID = "";
        this.schoolID = "";
        this.device = "";
        this.month_year = "";
        this.term_year = "";
        this.subject_term_year = "";
        this.year_month_day = "";
        this.present = "0";
        this.absent = "0";
        this.late = "0";
    }

    public TeacherAttendanceHeader(String className, String subject, String term, String date, String teacher, String noOfStudents, String noOfBoys, String noOfGirls, String device) {
        this.className = className;
        this.subject = subject;
        this.term = term;
        this.date = date;
        this.teacher = teacher;
        this.noOfStudents = noOfStudents;
        this.noOfBoys = noOfBoys;
        this.noOfGirls = noOfGirls;
        this.key = "";
        this.day = "";
        this.month = "";
        this.year = "";
        this.sortableDate = "";
        this.teacherID = "";
        this.classID = "";
        this.schoolID = "";
        this.device = device;
        this.month_year = "";
        this.term_year = "";
        this.subject_term_year = "";
        this.year_month_day = "";
        this.present = "0";
        this.absent = "0";
        this.late = "0";
    }

    public TeacherAttendanceHeader(String className, String subject, String term, String teacher, String noOfStudents, String noOfBoys, String noOfGirls, String day, String month, String year, String device) {
        this.className = className;
        this.subject = subject;
        this.term = term;
        this.date = "";
        this.teacher = teacher;
        this.noOfStudents = noOfStudents;
        this.noOfBoys = noOfBoys;
        this.noOfGirls = noOfGirls;
        this.key = "";
        this.day = day;
        this.month = month;
        this.year = year;
        this.sortableDate = "";
        this.teacherID = "";
        this.classID = "";
        this.schoolID = "";
        this.device = device;
        this.month_year = "";
        this.term_year = "";
        this.subject_term_year = "";
        this.year_month_day = "";
        this.present = "0";
        this.absent = "0";
        this.late = "0";
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getNoOfStudents() {
        return noOfStudents;
    }

    public void setNoOfStudents(String noOfStudents) {
        this.noOfStudents = noOfStudents;
    }

    public String getNoOfBoys() {
        return noOfBoys;
    }

    public void setNoOfBoys(String noOfBoys) {
        this.noOfBoys = noOfBoys;
    }

    public String getNoOfGirls() {
        return noOfGirls;
    }

    public void setNoOfGirls(String noOfGirls) {
        this.noOfGirls = noOfGirls;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getSortableDate() {
        return sortableDate;
    }

    public void setSortableDate(String sortableDate) {
        this.sortableDate = sortableDate;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
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

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public String getAbsent() {
        return absent;
    }

    public void setAbsent(String absent) {
        this.absent = absent;
    }

    public String getLate() {
        return late;
    }

    public void setLate(String late) {
        this.late = late;
    }
}
