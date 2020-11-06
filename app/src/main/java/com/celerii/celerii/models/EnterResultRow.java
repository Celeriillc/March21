package com.celerii.celerii.models;

/**
 * Created by DELL on 8/18/2017.
 */

public class EnterResultRow {
    String name, imageURL, score;
    String resultsID, teacherID, studentID, classID, schoolID;

    public EnterResultRow() {
        this.name = "";
        this.imageURL = "";
        this.score = "0";
        this.resultsID = "";
        this.teacherID = "";
        this.studentID = "";
        this.classID = "";
        this.schoolID = "";
    }

    public EnterResultRow(String name, String imageURL, String score) {
        this.name = name;
        this.imageURL = imageURL;
        this.score = score;
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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getResultsID() {
        return resultsID;
    }

    public void setResultsID(String resultsID) {
        this.resultsID = resultsID;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
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

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }
}
