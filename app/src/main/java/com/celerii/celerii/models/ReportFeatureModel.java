package com.celerii.celerii.models;

/**
 * Created by DELL on 1/5/2019.
 */

public class ReportFeatureModel {
    String feature, operatingSystem, message, senderID, senderAccountType, senderEMail, date, year, month, day;
    boolean read, responded;

    public ReportFeatureModel() {
    }

    public ReportFeatureModel(String feature, String operatingSystem, String message, String senderID, String senderAccountType, String senderEMail, String date, String year, String month, String day, boolean read, boolean responded) {
        this.feature = feature;
        this.operatingSystem = operatingSystem;
        this.message = message;
        this.senderID = senderID;
        this.senderAccountType = senderAccountType;
        this.senderEMail = senderEMail;
        this.date = date;
        this.year = year;
        this.month = month;
        this.day = day;
        this.read = read;
        this.responded = responded;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderAccountType() {
        return senderAccountType;
    }

    public void setSenderAccountType(String senderAccountType) {
        this.senderAccountType = senderAccountType;
    }

    public String getSenderEMail() {
        return senderEMail;
    }

    public void setSenderEMail(String senderEMail) {
        this.senderEMail = senderEMail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isResponded() {
        return responded;
    }

    public void setResponded(boolean responded) {
        this.responded = responded;
    }
}
