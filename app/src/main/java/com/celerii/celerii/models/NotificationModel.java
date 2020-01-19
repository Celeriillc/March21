package com.celerii.celerii.models;

/**
 * Created by DELL on 6/16/2018.
 */

public class NotificationModel {
    private String fromID, toID, fromAccountType, toAccountType, time, sortableTime, activityID, notificationType, notificationImageURL, object;
    boolean isSeen;

    private String fromProfilePicture, fromName, objectName;

    public NotificationModel() {
        this.fromID = "";
        this.toID = "";
        this.toAccountType = "";
        this.fromAccountType = "";
        this.time = "";
        this.sortableTime = "";
        this.activityID = "";
        this.notificationType = "";
        this.notificationImageURL = "";
        this.object = "";
        this.isSeen = false;
    }

    public NotificationModel(String fromID, String time, String sortableTime, String activityID, String notificationType, boolean isSeen) {
        this.fromID = fromID;
        this.time = time;
        this.sortableTime = sortableTime;
        this.activityID = activityID;
        this.notificationType = notificationType;
        this.isSeen = isSeen;
    }

    public NotificationModel(String fromID, String toID, String toAccountType, String fromAccountType, String time, String sortableTime,
                             String activityID, String notificationType, String notificationImageURL, String object, boolean isSeen) {
        this.fromID = fromID;
        this.toID = toID;
        this.toAccountType = toAccountType;
        this.fromAccountType = fromAccountType;
        this.time = time;
        this.sortableTime = sortableTime;
        this.activityID = activityID;
        this.notificationType = notificationType;
        this.notificationImageURL = notificationImageURL;
        this.object = object;
        this.isSeen = isSeen;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getToAccountType() {
        return toAccountType;
    }

    public void setToAccountType(String toAccountType) {
        this.toAccountType = toAccountType;
    }

    public String getFromAccountType() {
        return fromAccountType;
    }

    public void setFromAccountType(String fromAccountType) {
        this.fromAccountType = fromAccountType;
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

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationImageURL() {
        return notificationImageURL;
    }

    public void setNotificationImageURL(String notificationImageURL) {
        this.notificationImageURL = notificationImageURL;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getFromProfilePicture() {
        return fromProfilePicture;
    }

    public void setFromProfilePicture(String fromProfilePicture) {
        this.fromProfilePicture = fromProfilePicture;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
