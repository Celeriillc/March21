package com.celerii.celerii.models;

/**
 * Created by DELL on 5/9/2019.
 */

public class BehaviouralResultRowModel {
    String point, reward, className, classID, sortableDate;

    public BehaviouralResultRowModel() {
        this.point = "";
        this.reward = "";
        this.className = "";
    }

    public BehaviouralResultRowModel(String point, String reward, String className, String classID, String sortableDate) {
        this.point = point;
        this.reward = reward;
        this.className = className;
        this.classID = classID;
        this.sortableDate = sortableDate;
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
}
