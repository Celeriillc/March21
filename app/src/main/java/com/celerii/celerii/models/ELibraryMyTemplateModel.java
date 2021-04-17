package com.celerii.celerii.models;

public class ELibraryMyTemplateModel {
    String templateID, templateTitle, numberOfUses, date, sortableDate;

    public ELibraryMyTemplateModel() {
        this.templateID = "";
        this.templateTitle = "";
        this.numberOfUses = "";
        this.date = "";
        this.sortableDate = "";
    }

    public ELibraryMyTemplateModel(String templateID, String templateTitle, String numberOfUses, String date, String sortableDate) {
        this.templateID = templateID;
        this.templateTitle = templateTitle;
        this.numberOfUses = numberOfUses;
        this.date = date;
        this.sortableDate = sortableDate;
    }

    public String getTemplateID() {
        return templateID;
    }

    public void setTemplateID(String templateID) {
        this.templateID = templateID;
    }

    public String getTemplateTitle() {
        return templateTitle;
    }

    public void setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }

    public String getNumberOfUses() {
        return numberOfUses;
    }

    public void setNumberOfUses(String numberOfUses) {
        this.numberOfUses = numberOfUses;
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
