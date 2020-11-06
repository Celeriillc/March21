package com.celerii.celerii.models;

public class StudentsClassesModel {
    String studentID, classID;

    public StudentsClassesModel() {
        this.studentID = "";
        this.classID = "";
    }

    public StudentsClassesModel(String studentID, String classID) {
        this.studentID = studentID;
        this.classID = classID;
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
}
