package com.celerii.celerii.models;

/**
 * Created by DELL on 12/30/2019.
 */

public class ClassesStudentsAndParentsModel {
    String classID, schoolID, studentID, parentID;

    public ClassesStudentsAndParentsModel() {
        this.classID = "";
        this.schoolID = "";
        this.studentID = "";
        this.parentID = "";
    }

    public ClassesStudentsAndParentsModel(String classID, String schoolID, String studentID) {
        this.classID = classID;
        this.schoolID = schoolID;
        this.studentID = studentID;
    }

    public ClassesStudentsAndParentsModel(String classID, String schoolID, String studentID, String parentID) {
        this.classID = classID;
        this.schoolID = schoolID;
        this.studentID = studentID;
        this.parentID = parentID;
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

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }
}
