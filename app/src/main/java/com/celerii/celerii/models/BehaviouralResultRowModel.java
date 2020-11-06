package com.celerii.celerii.models;

/**
 * Created by DELL on 5/9/2019.
 */

public class BehaviouralResultRowModel {
    String key, point, reward, className, classID, sortableDate;
    Boolean isNew;

    public BehaviouralResultRowModel() {
        this.key = "";
        this.point = "";
        this.reward = "";
        this.className = "";
        this.classID = "";
        this.sortableDate = "";
        this.isNew = false;
    }

    public BehaviouralResultRowModel(String key, String point, String reward, String className, String classID, String sortableDate) {
        this.key = key;
        this.point = point;
        this.reward = reward;
        this.className = className;
        this.classID = classID;
        this.sortableDate = sortableDate;
        this.isNew = false;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getSortableDate() {
        return sortableDate;
    }

    public void setSortableDate(String sortableDate) {
        this.sortableDate = sortableDate;
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }
}
