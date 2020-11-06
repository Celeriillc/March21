package com.celerii.celerii.models;

public class RemoteCampaign {
    String campaignID, campaignURL, campaignBackgroundURL, campaignIconURL, campaignTitle, campaignText, campaignTarget;

    public RemoteCampaign() {
        this.campaignID = "";
        this.campaignURL = "";
        this.campaignBackgroundURL = "";
        this.campaignIconURL = "";
        this.campaignTitle = "";
        this.campaignText = "";
        this.campaignTarget = "";
    }

    public RemoteCampaign(String campaignID, String campaignURL, String campaignBackgroundURL, String campaignIconURL, String campaignTitle, String campaignText, String campaignTarget) {
        this.campaignID = campaignID;
        this.campaignURL = campaignURL;
        this.campaignBackgroundURL = campaignBackgroundURL;
        this.campaignIconURL = campaignIconURL;
        this.campaignTitle = campaignTitle;
        this.campaignText = campaignText;
        this.campaignTarget = campaignTarget;
    }

    public String getCampaignID() {
        return campaignID;
    }

    public void setCampaignID(String campaignID) {
        this.campaignID = campaignID;
    }

    public String getCampaignURL() {
        return campaignURL;
    }

    public void setCampaignURL(String campaignURL) {
        this.campaignURL = campaignURL;
    }

    public String getCampaignBackgroundURL() {
        return campaignBackgroundURL;
    }

    public void setCampaignBackgroundURL(String campaignBackgroundURL) {
        this.campaignBackgroundURL = campaignBackgroundURL;
    }

    public String getCampaignIconURL() {
        return campaignIconURL;
    }

    public void setCampaignIconURL(String campaignIconURL) {
        this.campaignIconURL = campaignIconURL;
    }

    public String getCampaignTitle() {
        return campaignTitle;
    }

    public void setCampaignTitle(String campaignTitle) {
        this.campaignTitle = campaignTitle;
    }

    public String getCampaignText() {
        return campaignText;
    }

    public void setCampaignText(String campaignText) {
        this.campaignText = campaignText;
    }

    public String getCampaignTarget() {
        return campaignTarget;
    }

    public void setCampaignTarget(String campaignTarget) {
        this.campaignTarget = campaignTarget;
    }
}
