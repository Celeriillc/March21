package com.celerii.celerii.models;

public class ELibraryAssignmentDetailHeaderModel {
    String assignmentID, title, classID, className, date, sortableDate;

    public ELibraryAssignmentDetailHeaderModel() {
        this.assignmentID = "";
        this.title = "";
        this.classID = "";
        this.className = "";
        this.date = "";
        this.sortableDate = "";
    }

    public ELibraryAssignmentDetailHeaderModel(String assignmentID, String title, String classID, String className, String date, String sortableDate) {
        this.assignmentID = assignmentID;
        this.title = title;
        this.classID = classID;
        this.className = className;
        this.date = date;
        this.sortableDate = sortableDate;
    }

    public String getAssignmentID() {
        return assignmentID;
    }

    public void setAssignmentID(String assignmentID) {
        this.assignmentID = assignmentID;
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
}
