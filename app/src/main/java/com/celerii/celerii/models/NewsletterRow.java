package com.celerii.celerii.models;

/**
 * Created by user on 7/27/2017.
 */

public class NewsletterRow {
    String newsletterKey, newsletterTitle, newsletterBody, schoolID, newsletterDate, newsletterHeaderImageURL, date, sortableDate;

    public NewsletterRow() {
        this.newsletterKey = "";
        this.newsletterTitle = "";
        this.newsletterBody = "";
        this.schoolID = "";
        this.newsletterDate = "";
        this.newsletterHeaderImageURL = "";
        this.date = "0000/00/00 00:00:00:000";
        this.sortableDate = "00000000000000000";
    }

    public String getNewsletterKey() {
        return newsletterKey;
    }

    public void setNewsletterKey(String newsletterKey) {
        this.newsletterKey = newsletterKey;
    }

    public String getNewsletterTitle() {
        return newsletterTitle;
    }

    public void setNewsletterTitle(String newsletterTitle) {
        this.newsletterTitle = newsletterTitle;
    }

    public String getNewsletterBody() {
        return newsletterBody;
    }

    public void setNewsletterBody(String newsletterBody) {
        this.newsletterBody = newsletterBody;
    }

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    public String getNewsletterDate() {
        return newsletterDate;
    }

    public void setNewsletterDate(String newsletterDate) {
        this.newsletterDate = newsletterDate;
    }

    public String getNewsletterHeaderImageURL() {
        return newsletterHeaderImageURL;
    }

    public void setNewsletterHeaderImageURL(String newsletterHeaderImageURL) {
        this.newsletterHeaderImageURL = newsletterHeaderImageURL;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSortableDate() {
        return sortableDate;
    }

    public void setSortableDate(String sortableDate) {
        this.sortableDate = sortableDate;
    }
}
