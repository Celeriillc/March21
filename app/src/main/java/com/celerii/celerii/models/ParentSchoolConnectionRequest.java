package com.celerii.celerii.models;

import java.util.ArrayList;

/**
 * Created by DELL on 4/6/2019.
 */

public class ParentSchoolConnectionRequest {
    String requestStatus, timeSent, sorttableTimeSent, requestSenderID, requestSenderName, requestSenderAccountType, requestSenderProfilePictureURL, studentID, studentName, studentProfilePictureURL;
    String requestKey, requestResponder, requestResponse;
    ArrayList<String> requestReciepients;

    public ParentSchoolConnectionRequest() {
        this.requestStatus = "";
        this.timeSent = "";
        this.sorttableTimeSent = "";
        this.requestSenderID = "";
        this.requestSenderName = "";
        this.requestSenderAccountType = "";
        this.requestSenderProfilePictureURL = "";
        this.studentID = "";
        this.studentName = "";
        this.studentProfilePictureURL = "";
        this.requestKey = "";
        this.requestResponder = "";
        this.requestResponse = "";
        this.requestReciepients = new ArrayList<>();
    }

    public ParentSchoolConnectionRequest(String requestStatus, String timeSent, String sorttableTimeSent, String requestSenderID, String requestSenderAccountType, String studentID, String requestKey, String requestResponder, String requestResponse, ArrayList<String> requestReciepients) {
        this.requestStatus = requestStatus;
        this.timeSent = timeSent;
        this.sorttableTimeSent = sorttableTimeSent;
        this.requestSenderID = requestSenderID;
        this.requestSenderName = "";
        this.requestSenderAccountType = requestSenderAccountType;
        this.requestSenderProfilePictureURL = "";
        this.studentID = studentID;
        this.studentName = "";
        this.studentProfilePictureURL = "";
        this.requestKey = requestKey;
        this.requestResponder = requestResponder;
        this.requestResponse = requestResponse;
        this.requestReciepients = requestReciepients;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
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

    public String getRequestSenderID() {
        return requestSenderID;
    }

    public void setRequestSenderID(String requestSenderID) {
        this.requestSenderID = requestSenderID;
    }

    public String getRequestSenderName() {
        return requestSenderName;
    }

    public void setRequestSenderName(String requestSenderName) {
        this.requestSenderName = requestSenderName;
    }

    public String getRequestSenderAccountType() {
        return requestSenderAccountType;
    }

    public void setRequestSenderAccountType(String requestSenderAccountType) {
        this.requestSenderAccountType = requestSenderAccountType;
    }

    public String getRequestSenderProfilePictureURL() {
        return requestSenderProfilePictureURL;
    }

    public void setRequestSenderProfilePictureURL(String requestSenderProfilePictureURL) {
        this.requestSenderProfilePictureURL = requestSenderProfilePictureURL;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentProfilePictureURL() {
        return studentProfilePictureURL;
    }

    public void setStudentProfilePictureURL(String studentProfilePictureURL) {
        this.studentProfilePictureURL = studentProfilePictureURL;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getRequestResponder() {
        return requestResponder;
    }

    public void setRequestResponder(String requestResponder) {
        this.requestResponder = requestResponder;
    }

    public String getRequestResponse() {
        return requestResponse;
    }

    public void setRequestResponse(String requestResponse) {
        this.requestResponse = requestResponse;
    }

    public ArrayList<String> getRequestReciepients() {
        return requestReciepients;
    }

    public void setRequestReciepients(ArrayList<String> requestReciepients) {
        this.requestReciepients = requestReciepients;
    }
}
