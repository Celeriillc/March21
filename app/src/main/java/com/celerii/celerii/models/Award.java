package com.celerii.celerii.models;

public class Award {
    String awardName, awardYear;

    public Award() {
        this.awardName = "";
        this.awardYear = "";
    }

    public Award(String awardName) {
        this.awardName = awardName;
        this.awardYear = "";
    }

    public String getAwardName() {
        return awardName;
    }

    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }

    public String getAwardYear() {
        return awardYear;
    }

    public void setAwardYear(String awardYear) {
        this.awardYear = awardYear;
    }
}
