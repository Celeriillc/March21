package com.celerii.celerii.models;

public class DisconnectionModel {
    String subjectID, objectID, disconnectionRef, time, sortableTime;

    public DisconnectionModel() {
        this.subjectID = "";
        this.objectID = "";
        this.disconnectionRef = "";
        this.time = "";
        this.sortableTime = "";
    }

    public DisconnectionModel(String subjectID, String objectID, String disconnectionRef, String time, String sortableTime) {
        this.subjectID = subjectID;
        this.objectID = objectID;
        this.disconnectionRef = disconnectionRef;
        this.time = time;
        this.sortableTime = sortableTime;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getDisconnectionRef() {
        return disconnectionRef;
    }

    public void setDisconnectionRef(String disconnectionRef) {
        this.disconnectionRef = disconnectionRef;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSortableTime() {
        return sortableTime;
    }

    public void setSortableTime(String sortableTime) {
        this.sortableTime = sortableTime;
    }
}
