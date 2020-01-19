package com.celerii.celerii.models;

/**
 * Created by user on 7/11/2017.
 */

public class GalleryModel{
    private String URL, ownerID;

    public GalleryModel() {
    }

    public GalleryModel(String URL, String ownerID) {
        this.URL = URL;
        this.ownerID = ownerID;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }
}
