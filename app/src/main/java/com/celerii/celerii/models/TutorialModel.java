package com.celerii.celerii.models;

public class TutorialModel {
    String createdBy, htmlBody, link, title, youtubeEmbedLink, youtubeVideoID;

    public TutorialModel() {
        this.createdBy = "";
        this.htmlBody = "";
        this.link = "";
        this.title = "";
        this.youtubeEmbedLink = "";
        this.youtubeVideoID = "";
    }

    public TutorialModel(String createdBy, String htmlBody, String link, String title, String youtubeEmbedLink, String youtubeVideoID) {
        this.createdBy = createdBy;
        this.htmlBody = htmlBody;
        this.link = link;
        this.title = title;
        this.youtubeEmbedLink = youtubeEmbedLink;
        this.youtubeVideoID = youtubeVideoID;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYoutubeEmbedLink() {
        return youtubeEmbedLink;
    }

    public void setYoutubeEmbedLink(String youtubeEmbedLink) {
        this.youtubeEmbedLink = youtubeEmbedLink;
    }

    public String getYoutubeVideoID() {
        return youtubeVideoID;
    }

    public void setYoutubeVideoID(String youtubeVideoID) {
        this.youtubeVideoID = youtubeVideoID;
    }
}
