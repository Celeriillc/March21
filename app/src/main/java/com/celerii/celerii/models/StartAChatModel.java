package com.celerii.celerii.models;

/**
 * Created by DELL on 11/20/2017.
 */

public class StartAChatModel {
    String name, otherInfo, profilePicURL;

    public StartAChatModel() {
    }

    public StartAChatModel(String name, String otherInfo, String profilePicURL) {
        this.name = name;
        this.otherInfo = otherInfo;
        this.profilePicURL = profilePicURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }
}
