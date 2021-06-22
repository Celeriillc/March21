package com.celerii.celerii.models;

public class ReminderModel {
    String activityID, accountType, reminderType, eventTitle, eventSender, eClassroomChildName, eClassroomChildID,
            eClassroomChildProfilePictureURL, eClassroomLink, eClassroomState, scheduledDate, originalScheduledDate;
    String timeToEvent;
    long timeInMilliseconds;

    public ReminderModel() {
        this.activityID = "";
        this.accountType = "";
        this.reminderType = "";
        this.eventTitle = "";
        this.eventSender = "";
        this.eClassroomChildName = "";
        this.eClassroomChildID = "";
        this.eClassroomChildProfilePictureURL = "";
        this.eClassroomLink = "";
        this.eClassroomState = "";
        this.scheduledDate = "";
        this.originalScheduledDate = "";
        this.timeToEvent = "";
        this.timeInMilliseconds = 0;
    }

    public ReminderModel(ReminderModel reminderModel) {
        this.activityID = reminderModel.activityID;
        this.accountType = reminderModel.accountType;
        this.reminderType = reminderModel.reminderType;
        this.eventTitle = reminderModel.eventTitle;
        this.eventSender = reminderModel.eventSender;
        this.eClassroomChildName = reminderModel.eClassroomChildName;
        this.eClassroomChildID = reminderModel.eClassroomChildID;
        this.eClassroomChildProfilePictureURL = reminderModel.eClassroomChildProfilePictureURL;
        this.eClassroomLink = reminderModel.eClassroomLink;
        this.eClassroomState = reminderModel.eClassroomState;
        this.scheduledDate = reminderModel.scheduledDate;
        this.originalScheduledDate = reminderModel.originalScheduledDate;
        this.timeToEvent = reminderModel.timeToEvent;
        this.timeInMilliseconds = reminderModel.timeInMilliseconds;
    }

    public ReminderModel(String activityID, String accountType, String reminderType, String eventTitle,
                         String eventSender, String eClassroomChildName, String eClassroomChildID,
                         String eClassroomChildProfilePictureURL, String eClassroomLink,
                         String eClassroomState, String scheduledDate, String originalScheduledDate,
                         String timeToEvent, long timeInMilliseconds) {
        this.activityID = activityID;
        this.accountType = accountType;
        this.reminderType = reminderType;
        this.eventTitle = eventTitle;
        this.eventSender = eventSender;
        this.eClassroomChildName = eClassroomChildName;
        this.eClassroomChildID = eClassroomChildID;
        this.eClassroomChildProfilePictureURL = eClassroomChildProfilePictureURL;
        this.eClassroomLink = eClassroomLink;
        this.eClassroomState = eClassroomState;
        this.scheduledDate = scheduledDate;
        this.originalScheduledDate = originalScheduledDate;
        this.timeToEvent = timeToEvent;
        this.timeInMilliseconds = timeInMilliseconds;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventSender() {
        return eventSender;
    }

    public void setEventSender(String eventSender) {
        this.eventSender = eventSender;
    }


    public String geteClassroomChildName() {
        return eClassroomChildName;
    }

    public void seteClassroomChildName(String eClassroomChildName) {
        this.eClassroomChildName = eClassroomChildName;
    }

    public String geteClassroomChildID() {
        return eClassroomChildID;
    }

    public void seteClassroomChildID(String eClassroomChildID) {
        this.eClassroomChildID = eClassroomChildID;
    }

    public String geteClassroomChildProfilePictureURL() {
        return eClassroomChildProfilePictureURL;
    }

    public void seteClassroomChildProfilePictureURL(String eClassroomChildProfilePictureURL) {
        this.eClassroomChildProfilePictureURL = eClassroomChildProfilePictureURL;
    }

    public String geteClassroomLink() {
        return eClassroomLink;
    }

    public void seteClassroomLink(String eClassroomLink) {
        this.eClassroomLink = eClassroomLink;
    }

    public String geteClassroomState() {
        return eClassroomState;
    }

    public void seteClassroomState(String eClassroomState) {
        this.eClassroomState = eClassroomState;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getOriginalScheduledDate() {
        return originalScheduledDate;
    }

    public void setOriginalScheduledDate(String originalScheduledDate) {
        this.originalScheduledDate = originalScheduledDate;
    }

    public String getTimeToEvent() {
        return timeToEvent;
    }

    public void setTimeToEvent(String timeToEvent) {
        this.timeToEvent = timeToEvent;
    }

    public long getTimeInMilliseconds() {
        return timeInMilliseconds;
    }

    public void setTimeInMilliseconds(long timeInMilliseconds) {
        this.timeInMilliseconds = timeInMilliseconds;
    }
}
