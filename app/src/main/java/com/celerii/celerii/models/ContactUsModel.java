package com.celerii.celerii.models;

/**
 * Created by DELL on 1/6/2019.
 */

public class ContactUsModel {
    String subject, message, senderID, senderAccountType, senderEMail, date, year, month, day;
    boolean read, responded;

    public ContactUsModel() {
    }

    public ContactUsModel(String subject, String message, String senderID, String senderAccountType, String senderEMail, String date, String year, String month, String day, boolean read, boolean responded) {
        this.subject = subject;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
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
