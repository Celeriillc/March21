package com.celerii.celerii.models;

/**
 * Created by user on 7/27/2017.
 */

public class NewsletterRow {
    String newsletterTitle, newsletterBody, newsletterPoster, newsletterDate, newsletterImageURL;
    int noOfViews, noOfFavorites, noOfComments;

    public NewsletterRow(String newsletterTitle, String newsletterBody, String newsletterPoster, String newsletterDate, String newsletterImageURL, int noOfViews, int noOfFavorites, int noOfComments) {
        this.newsletterTitle = newsletterTitle;
        this.newsletterBody = newsletterBody;
        this.newsletterPoster = newsletterPoster;
        this.newsletterDate = newsletterDate;
        this.newsletterImageURL = newsletterImageURL;
        this.noOfViews = noOfViews;
        this.noOfFavorites = noOfFavorites;
        this.noOfComments = noOfComments;
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

    public String getNewsletterPoster() {
        return newsletterPoster;
    }

    public void setNewsletterPoster(String newsletterPoster) {
        this.newsletterPoster = newsletterPoster;
    }

    public String getNewsletterDate() {
        return newsletterDate;
    }

    public void setNewsletterDate(String newsletterDate) {
        this.newsletterDate = newsletterDate;
    }

    public String getNewsletterImageURL() {
        return newsletterImageURL;
    }

    public void setNewsletterImageURL(String newsletterImageURL) {
        this.newsletterImageURL = newsletterImageURL;
    }

    public int getNoOfViews() {
        return noOfViews;
    }

    public void setNoOfViews(int noOfViews) {
        this.noOfViews = noOfViews;
    }

    public int getNoOfFavorites() {
        return noOfFavorites;
    }

    public void setNoOfFavorites(int noOfFavorites) {
        this.noOfFavorites = noOfFavorites;
    }

    public int getNoOfComments() {
        return noOfComments;
    }

    public void setNoOfComments(int noOfComments) {
        this.noOfComments = noOfComments;
    }
}
