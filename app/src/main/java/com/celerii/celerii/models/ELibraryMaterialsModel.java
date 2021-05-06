package com.celerii.celerii.models;

import com.celerii.celerii.helperClasses.Date;

import java.util.ArrayList;

public class ELibraryMaterialsModel {
    String materialID, title, description, author, ageGrade, materialURL, materialThumbnailURL, type, numberOfReads, approximateDuration;
    String date, sortableDate, tags;
    ArrayList<String> tagList;

    public ELibraryMaterialsModel() {
        this.materialID = "";
        this.title = "";
        this.description = "";
        this.author = "";
        this.ageGrade = "";
        this.materialURL = "";
        this.materialThumbnailURL = "";
        this.type = "";
        this.numberOfReads = "";
        this.approximateDuration = "";
        this.date = "";
        this.sortableDate = "0000/00/00 00:00:00:000";
        this.tags = "";
        this.tagList = new ArrayList<>();
    }

    public ELibraryMaterialsModel(String materialID, String title, String description, String author, String ageGrade, String materialURL, String materialThumbnailURL, String type, String numberOfReads, String approximateDuration, String date, String tags, ArrayList<String> tagList) {
        this.materialID = materialID;
        this.title = title;
        this.description = description;
        this.author = author;
        this.ageGrade = ageGrade;
        this.materialURL = materialURL;
        this.materialThumbnailURL = materialThumbnailURL;
        this.type = type;
        this.numberOfReads = numberOfReads;
        this.approximateDuration = approximateDuration;
        this.date = date;
        this.sortableDate = Date.convertToSortableDate(date);
        this.tags = tags;
        this.tagList = tagList;
    }

    public String getMaterialID() {
        return materialID;
    }

    public void setMaterialID(String materialID) {
        this.materialID = materialID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAgeGrade() {
        return ageGrade;
    }

    public void setAgeGrade(String ageGrade) {
        this.ageGrade = ageGrade;
    }

    public String getMaterialURL() {
        return materialURL;
    }

    public void setMaterialURL(String materialURL) {
        this.materialURL = materialURL;
    }

    public String getMaterialThumbnailURL() {
        return materialThumbnailURL;
    }

    public void setMaterialThumbnailURL(String materialThumbnailURL) {
        this.materialThumbnailURL = materialThumbnailURL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumberOfReads() {
        return numberOfReads;
    }

    public void setNumberOfReads(String numberOfReads) {
        this.numberOfReads = numberOfReads;
    }

    public String getApproximateDuration() {
        return approximateDuration;
    }

    public void setApproximateDuration(String approximateDuration) {
        this.approximateDuration = approximateDuration;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public ArrayList<String> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<String> tagList) {
        this.tagList = tagList;
    }
}
