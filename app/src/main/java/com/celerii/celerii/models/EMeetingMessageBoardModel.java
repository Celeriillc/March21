package com.celerii.celerii.models;

public class EMeetingMessageBoardModel {
    String scheduledMeetingID, senderID, senderName, senderProfilePictureURL, senderAccountType,
            fileURL, message, date, sortableDate;

    public EMeetingMessageBoardModel() {
        this.scheduledMeetingID = "";
        this.senderID = "";
        this.senderName = "";
        this.senderProfilePictureURL = "";
        this.senderAccountType = "";
        this.fileURL = "";
        this.message = "";
        this.date = "";
        this.sortableDate = "";
    }

    public EMeetingMessageBoardModel(String scheduledMeetingID, String senderID, String senderName, String senderProfilePictureURL, String senderAccountType, String fileURL, String message, String date, String sortableDate) {
        this.scheduledMeetingID = scheduledMeetingID;
        this.senderID = senderID;
        this.senderName = senderName;
        this.senderProfilePictureURL = senderProfilePictureURL;
        this.senderAccountType = senderAccountType;
        this.fileURL = fileURL;
        this.message = message;
        this.date = date;
        this.sortableDate = sortableDate;
    }

    public String getScheduledMeetingID() {
        return scheduledMeetingID;
    }

    public void setScheduledMeetingID(String scheduledMeetingID) {
        this.scheduledMeetingID = scheduledMeetingID;
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

    public String getSenderAccountType() {
        return senderAccountType;
    }

    public void setSenderAccountType(String senderAccountType) {
        this.senderAccountType = senderAccountType;
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
