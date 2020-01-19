package com.celerii.celerii.models;

/**
 * Created by DELL on 8/19/2017.
 */

public class TeacherPerformanceHeaderMain {
    String previousScore, currentScore, projectedScore;
    String previousClass, previousMaxObtainable, previousTerm, previousYear;
    String currentClass, currentMaxObtainable, currentTerm, currentYear;
    String projectedClass, projectedMaxObtainable, projectedTerm, projectedYear;
    Double[] yList;
    String[] xList;

    public TeacherPerformanceHeaderMain(Double[] yList, String[] xList) {
        this.previousScore = "";
        this.currentScore = "";
        this.projectedScore = "";
        this.previousClass = "";
        this.previousMaxObtainable = "";
        this.previousTerm = "";
        this.previousYear = "";
        this.currentClass = "";
        this.currentMaxObtainable = "";
        this.currentTerm = "";
        this.currentYear = "";
        this.projectedClass = "";
        this.projectedMaxObtainable = "";
        this.projectedTerm = "";
        this.projectedYear = "";
        this.yList = yList;
        this.xList = xList;
    }

    public TeacherPerformanceHeaderMain(String previousScore, String currentScore, String projectedScore, String previousClass, String previousMaxObtainable, String previousTerm, String previousYear, String currentClass, String currentMaxObtainable, String currentTerm, String currentYear, String projectedClass, String projectedMaxObtainable, String projectedTerm, String projectedYear, Double[] yList, String[] xList) {
        this.previousScore = previousScore;
        this.currentScore = currentScore;
        this.projectedScore = projectedScore;
        this.previousClass = previousClass;
        this.previousMaxObtainable = previousMaxObtainable;
        this.previousTerm = previousTerm;
        this.previousYear = previousYear;
        this.currentClass = currentClass;
        this.currentMaxObtainable = currentMaxObtainable;
        this.currentTerm = currentTerm;
        this.currentYear = currentYear;
        this.projectedClass = projectedClass;
        this.projectedMaxObtainable = projectedMaxObtainable;
        this.projectedTerm = projectedTerm;
        this.projectedYear = projectedYear;
        this.yList = yList;
        this.xList = xList;
    }

    public String getPreviousScore() {
        return previousScore;
    }

    public void setPreviousScore(String previousScore) {
        this.previousScore = previousScore;
    }

    public String getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(String currentScore) {
        this.currentScore = currentScore;
    }

    public String getProjectedScore() {
        return projectedScore;
    }

    public void setProjectedScore(String projectedScore) {
        this.projectedScore = projectedScore;
    }

    public String getPreviousClass() {
        return previousClass;
    }

    public void setPreviousClass(String previousClass) {
        this.previousClass = previousClass;
    }

    public String getPreviousMaxObtainable() {
        return previousMaxObtainable;
    }

    public void setPreviousMaxObtainable(String previousMaxObtainable) {
        this.previousMaxObtainable = previousMaxObtainable;
    }

    public String getPreviousTerm() {
        return previousTerm;
    }

    public void setPreviousTerm(String previousTerm) {
        this.previousTerm = previousTerm;
    }

    public String getPreviousYear() {
        return previousYear;
    }

    public void setPreviousYear(String previousYear) {
        this.previousYear = previousYear;
    }

    public String getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(String currentClass) {
        this.currentClass = currentClass;
    }

    public String getCurrentMaxObtainable() {
        return currentMaxObtainable;
    }

    public void setCurrentMaxObtainable(String currentMaxObtainable) {
        this.currentMaxObtainable = currentMaxObtainable;
    }

    public String getCurrentTerm() {
        return currentTerm;
    }

    public void setCurrentTerm(String currentTerm) {
        this.currentTerm = currentTerm;
    }

    public String getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(String currentYear) {
        this.currentYear = currentYear;
    }

    public String getProjectedClass() {
        return projectedClass;
    }

    public void setProjectedClass(String projectedClass) {
        this.projectedClass = projectedClass;
    }

    public String getProjectedMaxObtainable() {
        return projectedMaxObtainable;
    }

    public void setProjectedMaxObtainable(String projectedMaxObtainable) {
        this.projectedMaxObtainable = projectedMaxObtainable;
    }

    public String getProjectedTerm() {
        return projectedTerm;
    }

    public void setProjectedTerm(String projectedTerm) {
        this.projectedTerm = projectedTerm;
    }

    public String getProjectedYear() {
        return projectedYear;
    }

    public void setProjectedYear(String projectedYear) {
        this.projectedYear = projectedYear;
    }

    public Double[] getyList() {
        return yList;
    }

    public void setyList(Double[] yList) {
        this.yList = yList;
    }

    public String[] getxList() {
        return xList;
    }

    public void setxList(String[] xList) {
        this.xList = xList;
    }
}
