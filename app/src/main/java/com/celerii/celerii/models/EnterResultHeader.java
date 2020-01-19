package com.celerii.celerii.models;

/**
 * Created by DELL on 8/18/2017.
 */

public class EnterResultHeader {
    String subject, className, teacher, testType, maxScore, percentageOfTotal, date, term, day, month, year, sortableDate;
    String teacherID, classID, schoolID;
    Double previousPercentageOfTotal;

    public EnterResultHeader(String subject, String className, String teacher, String testType, String maxScore, String date, String term, String day, String month, String year, String teacherID, String classID) {
        this.subject = "";
        this.className = "";
        this.teacher = "";
        this.testType = "";
        this.maxScore = "";
        this.date = "";
        this.term = "";
        this.day = "";
        this.month = "";
        this.year = "";
        this.teacherID = "";
        this.classID = "";
    }

    public EnterResultHeader() {

    }

    public EnterResultHeader(String subject, String className, String teacher, String testType, String maxScore, String date, String term, String day, String month, String year) {
        this.subject = subject;
        this.className = className;
        this.teacher = teacher;
        this.testType = testType;
        this.maxScore = maxScore;
        this.date = date;
        this.term = term;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public EnterResultHeader(String subject, String className, String teacher, String testType, String maxScore, String date, String term) {
        this.subject = subject;
        this.className = className;
        this.teacher = teacher;
        this.testType = testType;
        this.maxScore = maxScore;
        this.date = date;
        this.term = term;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore;
    }

    public String getPercentageOfTotal() {
        return percentageOfTotal;
    }

    public void setPercentageOfTotal(String percentageOfTotal) {
        this.percentageOfTotal = percentageOfTotal;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public Double getPreviousPercentageOfTotal() {
        return previousPercentageOfTotal;
    }

    public void setPreviousPercentageOfTotal(Double previousPercentageOfTotal) {
        this.previousPercentageOfTotal = previousPercentageOfTotal;
    }
}
