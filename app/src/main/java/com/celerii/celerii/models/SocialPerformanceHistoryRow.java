package com.celerii.celerii.models;

/**
 * Created by DELL on 9/3/2017.
 */

public class SocialPerformanceHistoryRow {
    String title, className, teacher, point;
    Integer pointInt;

    public SocialPerformanceHistoryRow(String title, String className, String teacher, String point, Integer pointInt) {
        this.title = title;
        this.className = className;
        this.teacher = teacher;
        this.point = point;
        this.pointInt = pointInt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public Integer getPointInt() {
        return pointInt;
    }

    public void setPointInt(Integer pointInt) {
        this.pointInt = pointInt;
    }
}
