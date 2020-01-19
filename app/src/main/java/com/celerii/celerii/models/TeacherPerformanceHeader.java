package com.celerii.celerii.models;

/**
 * Created by DELL on 8/19/2017.
 */

public class TeacherPerformanceHeader {
    String previousHint, currentHint, projectedHint;

    public TeacherPerformanceHeader(String previousHint, String currentHint, String projectedHint) {
        this.previousHint = previousHint;
        this.currentHint = currentHint;
        this.projectedHint = projectedHint;
    }

    public TeacherPerformanceHeader() {
    }

    public String getPreviousHint() {
        return previousHint;
    }

    public void setPreviousHint(String previousHint) {
        this.previousHint = previousHint;
    }

    public String getCurrentHint() {
        return currentHint;
    }

    public void setCurrentHint(String currentHint) {
        this.currentHint = currentHint;
    }

    public String getProjectedHint() {
        return projectedHint;
    }

    public void setProjectedHint(String projectedHint) {
        this.projectedHint = projectedHint;
    }
}
