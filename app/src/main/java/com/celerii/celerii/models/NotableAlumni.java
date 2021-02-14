package com.celerii.celerii.models;

public class NotableAlumni {
    String name, note, profilePictureURL, set;

    public NotableAlumni() {
        this.name = "";
        this.note = "";
        this.profilePictureURL = "";
        this.set = "";
    }

    public NotableAlumni(String name) {
        this.name = name;
        this.note = "";
        this.profilePictureURL = "";
        this.set = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public void setProfilePictureURL(String profilePictureURL) {
        this.profilePictureURL = profilePictureURL;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }
}
