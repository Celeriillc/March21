package com.celerii.celerii.models;

public class TutorialModel {
    String tutorialTitle, tutorialLink;

    public TutorialModel() {
        this.tutorialTitle = "";
        this.tutorialLink = "";
    }

    public TutorialModel(String tutorialTitle, String tutorialLink) {
        this.tutorialTitle = tutorialTitle;
        this.tutorialLink = tutorialLink;
    }

    public String getTutorialTitle() {
        return tutorialTitle;
    }

    public void setTutorialTitle(String tutorialTitle) {
        this.tutorialTitle = tutorialTitle;
    }

    public String getTutorialLink() {
        return tutorialLink;
    }

    public void setTutorialLink(String tutorialLink) {
        this.tutorialLink = tutorialLink;
    }
}
