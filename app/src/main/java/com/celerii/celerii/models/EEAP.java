package com.celerii.celerii.models;

public class EEAP {
    String average, color, examName;

    public EEAP() {
        this.average = "";
        this.color = "";
        this.examName = "";
    }

    public EEAP(String examName) {
        this.average = "";
        this.color = "";
        this.examName = examName;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }
}
