package com.celerii.celerii.models;

/**
 * Created by DELL on 1/6/2019.
 */

public class ReportAbuseModel {
    String header, report, senderID, senderAccountType, senderEMail, reporteeID, date, year, month, day;
    boolean read, responded;

    public ReportAbuseModel() {
    }

    public ReportAbuseModel(String header, String report, String senderID, String senderAccountType, String senderEMail, String reporteeID, String date, String year, String month, String day, boolean read, boolean responded) {
        this.header = header;
        this.report = report;
        this.senderID = senderID;
        this.senderAccountType = senderAccountType;
        this.senderEMail = senderEMail;
        this.reporteeID = reporteeID;
        this.date = date;
        this.year = year;
        this.month = month;
        this.day = day;
        this.read = read;
        this.responded = responded;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getSenderAccountType() {
        return senderAccountType;
    }

    public void setSenderAccountType(String senderAccountType) {
        this.senderAccountType = senderAccountType;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderEMail() {
        return senderEMail;
    }

    public void setSenderEMail(String senderEMail) {
        this.senderEMail = senderEMail;
    }

    public String getReporteeID() {
        return reporteeID;
    }

    public void setReporteeID(String reporteeID) {
        this.reporteeID = reporteeID;
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
