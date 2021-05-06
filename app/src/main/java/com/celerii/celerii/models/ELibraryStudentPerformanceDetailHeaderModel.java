package com.celerii.celerii.models;

public class ELibraryStudentPerformanceDetailHeaderModel {
    String studentID, studentName, title, classID, className, date, sortableDate, score;

    public ELibraryStudentPerformanceDetailHeaderModel() {
        this.studentID = "";
        this.studentName = "";
        this.title = "";
        this.classID = "";
        this.className = "";
        this.date = "";
        this.sortableDate = "";
        this.score = "";
    }

    public ELibraryStudentPerformanceDetailHeaderModel(String studentID, String studentName, String score) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.title = "";
        this.classID = "";
        this.className = "";
        this.date = "";
        this.sortableDate = "";
        this.score = score;
    }

    public ELibraryStudentPerformanceDetailHeaderModel(String studentID, String studentName, String title, String classID, String className, String date, String sortableDate, String score) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.title = title;
        this.classID = classID;
        this.className = className;
        this.date = date;
        this.sortableDate = sortableDate;
        this.score = score;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
