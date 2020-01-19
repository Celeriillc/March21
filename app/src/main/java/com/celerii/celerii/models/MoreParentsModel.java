package com.celerii.celerii.models;

/**
 * Created by DELL on 1/20/2018.
 */

public class MoreParentsModel {
    String childId, childName, childPicUrl;

    public MoreParentsModel() {
    }

    public MoreParentsModel(String childId, String childName, String childPicUrl) {
        this.childId = childId;
        this.childName = childName;
        this.childPicUrl = childPicUrl;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildPicUrl() {
        return childPicUrl;
    }

    public void setChildPicUrl(String childPicUrl) {
        this.childPicUrl = childPicUrl;
    }
}
