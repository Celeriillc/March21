package com.celerii.celerii.models;

public class EClassroomParticipantsModel {
    String id, name, profilePictureURL;
    Boolean wasPresent;

    public EClassroomParticipantsModel() {
        this.id = "";
        this.name = "";
        this.profilePictureURL = "";
        this.wasPresent = false;
    }

    public EClassroomParticipantsModel(String id, Boolean wasPresent) {
        this.id = id;
        this.name = "";
        this.profilePictureURL = "";
        this.wasPresent = wasPresent;
    }

    public EClassroomParticipantsModel(String id, String name, String profilePictureURL, Boolean wasPresent) {
        this.id = id;
        this.name = name;
        this.profilePictureURL = profilePictureURL;
        this.wasPresent = wasPresent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public void setProfilePictureURL(String profilePictureURL) {
        this.profilePictureURL = profilePictureURL;
    }

    public Boolean getWasPresent() {
        return wasPresent;
    }

    public void setWasPresent(Boolean wasPresent) {
        this.wasPresent = wasPresent;
    }
}
