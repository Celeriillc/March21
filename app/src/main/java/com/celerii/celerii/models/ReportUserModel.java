package com.celerii.celerii.models;

/**
 * Created by DELL on 5/12/2018.
 */

public class ReportUserModel {
    String name, URL, userID;

    public ReportUserModel() {
    }

    public ReportUserModel(String name, String URL, String userID) {
        this.name = name;
        this.URL = URL;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
