package com.celerii.celerii.models;

/**
 * Created by DELL on 8/31/2017.
 */

public class SetupSchoolListRow {
    String schoolId, schoolName, schoolAddress, schoolImageURL;
    boolean connected;

    public SetupSchoolListRow() {
    }

    public SetupSchoolListRow(String schoolId, String schoolName, String schoolAddress, String schoolImageURL, boolean connected) {
        this.schoolId = schoolId;
        this.schoolName = schoolName;
        this.schoolAddress = schoolAddress;
        this.schoolImageURL = schoolImageURL;
        this.connected = connected;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getSchoolImageURL() {
        return schoolImageURL;
    }

    public void setSchoolImageURL(String schoolImageURL) {
        this.schoolImageURL = schoolImageURL;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
