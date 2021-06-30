package com.celerii.celerii.models;

/**
 * Created by DELL on 9/11/2017.
 */

public class Class {
    String className, classPicURL, id, classTeacher;
    boolean isTicked, isDeleted;

    public Class() {
        this.className = "";
        this.classPicURL = "";
        this.id = id;
        this.classTeacher = "";
        this.isTicked = false;
        this.isDeleted = false;
    }

    public Class(String className, String id, boolean isTicked) {
        this.className = className;
        this.classPicURL = "";
        this.id = id;
        this.classTeacher = "";
        this.isTicked = isTicked;
        this.isDeleted = false;
    }

    public Class(boolean isTicked, String className) {
        this.className = className;
        this.classPicURL = "";
        this.id = id;
        this.classTeacher = "";
        this.isTicked = isTicked;
        this.isDeleted = false;
    }

    public Class(String className, String classPicURL, String id, boolean isTicked) {
        this.className = className;
        this.classPicURL = classPicURL;
        this.id = id;
        this.classTeacher = "";
        this.isTicked = isTicked;
        this.isDeleted = false;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassPicURL() {
        return classPicURL;
    }

    public void setClassPicURL(String classPicURL) {
        this.classPicURL = classPicURL;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(String classTeacher) {
        this.classTeacher = classTeacher;
    }

    public boolean isTicked() {
        return isTicked;
    }

    public void setTicked(boolean ticked) {
        isTicked = ticked;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
