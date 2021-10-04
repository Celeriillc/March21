package com.celerii.celerii.models;

/**
 * Created by DELL on 5/9/2019.
 */

public class BehaviouralResultsHeaderModel {
    String term, year, totalPointsEarned, totalPointsFined, pointsEarnedThisTerm, pointsFinedThisTerm, errorMessage;

    public BehaviouralResultsHeaderModel() {
        this.term = "";
        this.year = "";
        this.totalPointsEarned = "";
        this.totalPointsFined = "";
        this.pointsEarnedThisTerm = "";
        this.pointsFinedThisTerm = "";
        this.errorMessage = "";
    }

    public BehaviouralResultsHeaderModel(String term, String year, String totalPointsEarned, String totalPointsFined, String pointsEarnedThisTerm, String pointsFinedThisTerm) {
        this.term = term;
        this.year = year;
        this.totalPointsEarned = totalPointsEarned;
        this.totalPointsFined = totalPointsFined;
        this.pointsEarnedThisTerm = pointsEarnedThisTerm;
        this.pointsFinedThisTerm = pointsFinedThisTerm;
        this.errorMessage = "";
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTotalPointsEarned() {
        return totalPointsEarned;
    }

    public void setTotalPointsEarned(String totalPointsEarned) {
        this.totalPointsEarned = totalPointsEarned;
    }

    public String getTotalPointsFined() {
        return totalPointsFined;
    }

    public void setTotalPointsFined(String totalPointsFined) {
        this.totalPointsFined = totalPointsFined;
    }

    public String getPointsEarnedThisTerm() {
        return pointsEarnedThisTerm;
    }

    public void setPointsEarnedThisTerm(String pointsEarnedThisTerm) {
        this.pointsEarnedThisTerm = pointsEarnedThisTerm;
    }

    public String getPointsFinedThisTerm() {
        return pointsFinedThisTerm;
    }

    public void setPointsFinedThisTerm(String pointsFinedThisTerm) {
        this.pointsFinedThisTerm = pointsFinedThisTerm;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
