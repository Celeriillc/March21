package com.celerii.celerii.models;

public class ELibraryStudentPerformanceHomeModel {
    String studentID, studentName, studentProfilePictureURL, score;

    public ELibraryStudentPerformanceHomeModel() {
        this.studentID = "";
        this.studentName = "";
        this.studentProfilePictureURL = "";
        this.score = "";
    }

    public ELibraryStudentPerformanceHomeModel(String studentID, String score) {
        this.studentID = studentID;
        this.studentName = "";
        this.studentProfilePictureURL = "";
        this.score = score;
    }

    public ELibraryStudentPerformanceHomeModel(String studentID, String studentName, String studentProfilePictureURL, String score) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.studentProfilePictureURL = studentProfilePictureURL;
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

    public String getStudentProfilePictureURL() {
        return studentProfilePictureURL;
    }

    public void setStudentProfilePictureURL(String studentProfilePictureURL) {
        this.studentProfilePictureURL = studentProfilePictureURL;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
