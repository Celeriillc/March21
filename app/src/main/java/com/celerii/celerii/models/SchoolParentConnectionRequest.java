package com.celerii.celerii.models;

/**
 * Created by DELL on 4/6/2019.
 */

public class SchoolParentConnectionRequest {
    String status, timeSent, sorttableTimeSent, parent, student, school;

    public SchoolParentConnectionRequest() {
        this.status = "";
        this.timeSent = "";
        this.sorttableTimeSent = "";
        this.parent = "";
        this.student = "";
        this.school = "";
    }

    public SchoolParentConnectionRequest(String status, String timeSent, String sorttableTimeSent, String parent, String student, String school) {
        this.status = status;
        this.timeSent = timeSent;
        this.sorttableTimeSent = sorttableTimeSent;
        this.parent = parent;
        this.student = student;
        this.school = school;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }

    public String getSorttableTimeSent() {
        return sorttableTimeSent;
    }

    public void setSorttableTimeSent(String sorttableTimeSent) {
        this.sorttableTimeSent = sorttableTimeSent;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
