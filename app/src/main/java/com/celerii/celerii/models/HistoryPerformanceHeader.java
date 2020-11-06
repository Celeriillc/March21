package com.celerii.celerii.models;

/**
 * Created by user on 7/23/2017.
 */

public class HistoryPerformanceHeader {
    String subjectHead, averageScore, previousScore;
    Double[] yList;
    String[] xList;

    public HistoryPerformanceHeader() {
        this.subjectHead = "";
        this.averageScore = "";
        this.previousScore = "";
        this.yList = new Double[0];
        this.xList = new String[0];
    }

    public HistoryPerformanceHeader(String subjectHead, String averageScore, String previousScore, Double[] yList, String[] xList) {
        this.subjectHead = subjectHead;
        this.averageScore = averageScore;
        this.previousScore = previousScore;
        this.yList = yList;
        this.xList = xList;
    }

    public String getSubjectHead() {
        return subjectHead;
    }

    public void setSubjectHead(String subjectHead) {
        this.subjectHead = subjectHead;
    }

    public String getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(String averageScore) {
        this.averageScore = averageScore;
    }

    public String getPreviousScore() {
        return previousScore;
    }

    public void setPreviousScore(String previousScore) {
        this.previousScore = previousScore;
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
