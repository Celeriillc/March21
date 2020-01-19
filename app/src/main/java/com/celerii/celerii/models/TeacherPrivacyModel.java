package com.celerii.celerii.models;

/**
 * Created by DELL on 1/7/2018.
 */

public class TeacherPrivacyModel {
    boolean timeline, phoneNumber, maritalStatus, location;

    public TeacherPrivacyModel() {
    }

    public TeacherPrivacyModel(boolean timeline, boolean location, boolean phoneNumber, boolean maritalStatus) {
        this.timeline = timeline;
        this.phoneNumber = phoneNumber;
        this.maritalStatus = maritalStatus;
        this.location = location;
    }

    public boolean isTimeline() {
        return timeline;
    }

    public void setTimeline(boolean timeline) {
        this.timeline = timeline;
    }

    public boolean isPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(boolean phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(boolean maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public boolean isLocation() {
        return location;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }
}
