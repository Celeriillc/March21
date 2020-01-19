package com.celerii.celerii.models;

/**
 * Created by DELL on 12/17/2017.
 */

public class ManageClassesModel {
    String name, picURL, classId;

    public ManageClassesModel() {
    }

    public ManageClassesModel(String name, String picURL, String classId) {
        this.name = name;
        this.picURL = picURL;
        this.classId = classId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}
