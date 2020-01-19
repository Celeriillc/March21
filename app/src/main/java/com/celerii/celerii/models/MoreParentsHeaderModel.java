package com.celerii.celerii.models;

/**
 * Created by DELL on 1/20/2018.
 */

public class MoreParentsHeaderModel {
    String parentID, parentName, parentImageURL;

    public MoreParentsHeaderModel() {
    }

    public MoreParentsHeaderModel(String parentName, String parentImageURL) {
        this.parentName = parentName;
        this.parentImageURL = parentImageURL;
    }

    public MoreParentsHeaderModel(String parentID, String parentName, String parentImageURL) {
        this.parentID = parentID;
        this.parentName = parentName;
        this.parentImageURL = parentImageURL;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentImageURL() {
        return parentImageURL;
    }

    public void setParentImageURL(String parentImageURL) {
        this.parentImageURL = parentImageURL;
    }
}
