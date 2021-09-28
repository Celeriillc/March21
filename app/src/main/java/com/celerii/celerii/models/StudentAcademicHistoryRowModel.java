package com.celerii.celerii.models;

public class StudentAcademicHistoryRowModel {
    String studentID, name, imageURL;
    String average;

    public StudentAcademicHistoryRowModel() {
        this.studentID = "";
        this.name = "";
        this.imageURL = "";
        this.average = "";
    }

    public StudentAcademicHistoryRowModel(String studentID, String name, String imageURL) {
        this.studentID = studentID;
        this.name = name;
        this.imageURL = imageURL;
        this.average = "";
    }

    public StudentAcademicHistoryRowModel(String studentID, String average) {
        this.studentID = studentID;
        this.name = "";
        this.imageURL = "";
        this.average = average;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }
}
