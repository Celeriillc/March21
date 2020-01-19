package com.celerii.celerii.models;

/**
 * Created by DELL on 3/27/2019.
 */

public class TeacherSchoolRequest {
    String senderID, recieverID, time, sorttableTime;
    boolean responded, accepted, declined;

    public TeacherSchoolRequest() {
        senderID = "";
        recieverID = "";
        responded = false;
        accepted = false;
        declined = false;
        time = "";
        sorttableTime = "";
    }

    public TeacherSchoolRequest(String senderID, String recieverID, String time, String sorttableTime, boolean responded, boolean accepted, boolean declined) {
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.time = time;
        this.sorttableTime = sorttableTime;
        this.responded = responded;
        this.accepted = accepted;
        this.declined = declined;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getRecieverID() {
        return recieverID;
    }

    public void setRecieverID(String recieverID) {
        this.recieverID = recieverID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSorttableTime() {
        return sorttableTime;
    }

    public void setSorttableTime(String sorttableTime) {
        this.sorttableTime = sorttableTime;
    }

    public boolean isResponded() {
        return responded;
    }

    public void setResponded(boolean responded) {
        this.responded = responded;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isDeclined() {
        return declined;
    }

    public void setDeclined(boolean declined) {
        this.declined = declined;
    }
}
