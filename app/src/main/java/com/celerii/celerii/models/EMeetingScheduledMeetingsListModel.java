package com.celerii.celerii.models;

public class EMeetingScheduledMeetingsListModel {
    String scheduledMeetingID, scheduledMeetingTitle, schoolName, schoolID, schoolImage, dateCreated, sortableDateCreated, dateScheduled, sortableDateScheduled,
            description, meetingLink;
    Boolean open;

    public EMeetingScheduledMeetingsListModel() {
        this.scheduledMeetingID = "";
        this.scheduledMeetingTitle = "";
        this.schoolName = "";
        this.schoolID = "";
        this.schoolImage = "";
        this.dateCreated = "";
        this.sortableDateCreated = "";
        this.dateScheduled = "";
        this.sortableDateScheduled = "";
        this.description = "";
        this.meetingLink = "";
        this.open = true;
    }

    public String getScheduledMeetingID() {
        return scheduledMeetingID;
    }

    public void setScheduledMeetingID(String scheduledMeetingID) {
        this.scheduledMeetingID = scheduledMeetingID;
    }

    public String getScheduledMeetingTitle() {
        return scheduledMeetingTitle;
    }

    public void setScheduledMeetingTitle(String scheduledMeetingTitle) {
        this.scheduledMeetingTitle = scheduledMeetingTitle;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    public String getSchoolImage() {
        return schoolImage;
    }

    public void setSchoolImage(String schoolImage) {
        this.schoolImage = schoolImage;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getSortableDateCreated() {
        return sortableDateCreated;
    }

    public void setSortableDateCreated(String sortableDateCreated) {
        this.sortableDateCreated = sortableDateCreated;
    }

    public String getDateScheduled() {
        return dateScheduled;
    }

    public void setDateScheduled(String dateScheduled) {
        this.dateScheduled = dateScheduled;
    }

    public String getSortableDateScheduled() {
        return sortableDateScheduled;
    }

    public void setSortableDateScheduled(String sortableDateScheduled) {
        this.sortableDateScheduled = sortableDateScheduled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }
}
