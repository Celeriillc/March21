package com.celerii.celerii.models;

/**
 * Created by DELL on 1/23/2019.
 */

public class KidScoreForTeachersModel {
    String kidName, kidProfilePicture, kidScore, kidID;

    public KidScoreForTeachersModel() {
    }

    public KidScoreForTeachersModel(String kidName, String kidProfilePicture, String kidScore) {
        this.kidName = kidName;
        this.kidProfilePicture = kidProfilePicture;
        this.kidScore = kidScore;
    }

    public String getKidName() {
        return kidName;
    }

    public void setKidName(String kidName) {
        this.kidName = kidName;
    }

    public String getKidProfilePicture() {
        return kidProfilePicture;
    }

    public void setKidProfilePicture(String kidProfilePicture) {
        this.kidProfilePicture = kidProfilePicture;
    }

    public String getKidScore() {
        return kidScore;
    }

    public void setKidScore(String kidScore) {
        this.kidScore = kidScore;
    }

    public String getKidID() {
        return kidID;
    }

    public void setKidID(String kidID) {
        this.kidID = kidID;
    }
}
