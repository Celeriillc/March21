package com.celerii.celerii.models;

/**
 * Created by DELL on 11/8/2017.
 */

public class MoreTeacherHeaderModel {
    String teacherID, teacherName, teacherImageURL;

    public MoreTeacherHeaderModel() {
    }

    public MoreTeacherHeaderModel(String teacherName, String teacherImageURL) {
        this.teacherName = teacherName;
        this.teacherImageURL = teacherImageURL;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherImageURL() {
        return teacherImageURL;
    }

    public void setTeacherImageURL(String teacherImageURL) {
        this.teacherImageURL = teacherImageURL;
    }
}
