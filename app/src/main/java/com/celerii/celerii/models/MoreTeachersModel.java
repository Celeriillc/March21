package com.celerii.celerii.models;

/**
 * Created by DELL on 11/8/2017.
 */

public class MoreTeachersModel {
    String classId, className, classPicUrl;

    public MoreTeachersModel() {
        this.classId = "";
        this.className = "";
        this.classPicUrl = "";
    }

    public MoreTeachersModel(String classId, String className) {
        this.classId = classId;
        this.className = className;
        this.classPicUrl = "";
    }

    public MoreTeachersModel(String classId, String className, String classPicUrl) {
        this.classId = classId;
        this.className = className;
        this.classPicUrl = classPicUrl;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassPicUrl() {
        return classPicUrl;
    }

    public void setClassPicUrl(String classPicUrl) {
        this.classPicUrl = classPicUrl;
    }
}
