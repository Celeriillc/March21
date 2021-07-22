package com.celerii.celerii.models;

public class EClassroomMessageBoardModel {
    String scheduledClassID, senderID, senderName, senderProfilePictureURL, fileURL, message, date, sortableDate;

    public EClassroomMessageBoardModel() {
        this.scheduledClassID = "";
        this.senderID = "";
        this.senderName = "";
        this.senderProfilePictureURL = "";
        this.fileURL = "";
        this.message = "";
        this.date = "";
        this.sortableDate = "";
    }

    public EClassroomMessageBoardModel(String scheduledClassID, String senderID, String senderName, String senderProfilePictureURL, String fileURL, String message, String date, String sortableDate) {
        this.scheduledClassID = scheduledClassID;
        this.senderID = senderID;
        this.senderName = senderName;
        this.senderProfilePictureURL = senderProfilePictureURL;
        this.fileURL = fileURL;
        this.message = message;
        this.date = date;
        this.sortableDate = sortableDate;
    }

    public String getScheduledClassID() {
        return scheduledClassID;
    }

    public void setScheduledClassID(String scheduledClassID) {
        this.scheduledClassID = scheduledClassID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderProfilePictureURL() {
        return senderProfilePictureURL;
    }

    public void setSenderProfilePictureURL(String senderProfilePictureURL) {
        this.senderProfilePictureURL = senderProfilePictureURL;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSortableDate() {
        return sortableDate;
    }

    public void setSortableDate(String sortableDate) {
        this.sortableDate = sortableDate;
    }
}
