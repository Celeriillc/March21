package com.celerii.celerii.models;

/**
 * Created by DELL on 9/24/2017.
 */

public class ClassStoryNotification {
    String postID, date, sortableDate;

    public ClassStoryNotification(String postID, String date, String sortableDate) {
        this.postID = postID;
        this.date = date;
        this.sortableDate = sortableDate;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSortableDate() {
        return sortableDate;
    }

    public void setSortableDate(String sortableDate) {
        this.sortableDate = sortableDate;
    }
}
