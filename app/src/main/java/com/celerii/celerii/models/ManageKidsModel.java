package com.celerii.celerii.models;

/**
 * Created by DELL on 8/14/2017.
 */

public class ManageKidsModel {
    String name, picURL, id;

    public ManageKidsModel() {
        this.name = "";
        this.picURL = "";
        this.id = "";
    }

    public ManageKidsModel(String name, String picURL, String id) {
        this.name = name;
        this.picURL = picURL;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }
}
