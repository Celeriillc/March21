package com.celerii.celerii.models;

/**
 * Created by DELL on 9/24/2017.
 */

public class LikeNotification {
    String likerID, likeTime;

    public LikeNotification(String likerID, String likeTime) {
        this.likerID = likerID;
        this.likeTime = likeTime;
    }

    public String getLikerID() {
        return likerID;
    }

    public void setLikerID(String likerID) {
        this.likerID = likerID;
    }

    public String getLikeTime() {
        return likeTime;
    }

    public void setLikeTime(String likeTime) {
        this.likeTime = likeTime;
    }
}
