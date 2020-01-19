package com.celerii.celerii.models;

public class StudentsSchoolsClassesandTeachersModel {
    String studentID, schoolID, classID, teacherID;

    public StudentsSchoolsClassesandTeachersModel() {
        this.studentID = "";
        this.schoolID = "";
        this.classID = "";
        this.teacherID = "";
    }

    public StudentsSchoolsClassesandTeachersModel(String studentID, String schoolID, String classID, String teacherID) {
        this.studentID = studentID;
        this.schoolID = schoolID;
        this.classID = classID;
        this.teacherID = teacherID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
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

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }
}
