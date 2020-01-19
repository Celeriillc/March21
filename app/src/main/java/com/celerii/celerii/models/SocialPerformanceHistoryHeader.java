package com.celerii.celerii.models;

/**
 * Created by DELL on 9/3/2017.
 */

public class SocialPerformanceHistoryHeader {
    String pointsAwarded, pointsFined, totalPoints, primaryTemp, secondaryTemp;

    public SocialPerformanceHistoryHeader(String pointsAwarded, String pointsFined, String totalPoints, String primaryTemp, String secondaryTemp) {
        this.pointsAwarded = pointsAwarded;
        this.pointsFined = pointsFined;
        this.totalPoints = totalPoints;
        this.primaryTemp = primaryTemp;
        this.secondaryTemp = secondaryTemp;
    }

    public String getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(String pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    public String getPointsFined() {
        return pointsFined;
    }

    public void setPointsFined(String pointsFined) {
        this.pointsFined = pointsFined;
    }

    public String getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(String totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getPrimaryTemp() {
        return primaryTemp;
    }

    public void setPrimaryTemp(String primaryTemp) {
        this.primaryTemp = primaryTemp;
    }

    public String getSecondaryTemp() {
        return secondaryTemp;
    }

    public void setSecondaryTemp(String secondaryTemp) {
        this.secondaryTemp = secondaryTemp;
    }
}
