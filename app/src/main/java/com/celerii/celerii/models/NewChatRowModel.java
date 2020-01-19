package com.celerii.celerii.models;

/**
 * Created by DELL on 6/5/2018.
 */

public class NewChatRowModel {
    String name, relationship, profilePicURL, IDofPartner;

    public NewChatRowModel() {
    }

    public NewChatRowModel(String name, String relationship, String profilePicURL, String IDofPartner) {
        this.name = name;
        this.relationship = relationship;
        this.profilePicURL = profilePicURL;
        this.IDofPartner = IDofPartner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public String getIDofPartner() {
        return IDofPartner;
    }

    public void setIDofPartner(String IDofPartner) {
        this.IDofPartner = IDofPartner;
    }
}
