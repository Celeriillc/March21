package com.celerii.celerii.models;

/**
 * Created by DELL on 9/11/2017.
 */

public class Class {
    String className, classPicURL, id;
    boolean isTicked;

    public Class() {
    }

    public Class(String className, String classPicURL, boolean isTicked) {
        this.className = className;
        this.classPicURL = classPicURL;
        this.isTicked = isTicked;
    }

    public Class(boolean isTicked, String className) {
        this.isTicked = isTicked;
        this.className = className;
    }

    public Class(String className, String classPicURL, String id, boolean isTicked) {
        this.className = className;
        this.classPicURL = classPicURL;
        this.id = id;
        this.isTicked = isTicked;
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

    public boolean isTicked() {
        return isTicked;
    }

    public void setTicked(boolean ticked) {
        isTicked = ticked;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }
}
