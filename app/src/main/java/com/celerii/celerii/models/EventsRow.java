package com.celerii.celerii.models;

/**
 * Created by user on 7/27/2017.
 */

public class EventsRow {
    String eventTitle, eventDate, eventSortableDate, eventDescription, schoolID, key;
    Boolean isNew;

    public EventsRow() {
        this.eventTitle = "";
        this.eventDate = "";
        this.eventSortableDate = "";
        this.eventDescription = "";
        this.schoolID = "";
        this.key = "";
        this.isNew = false;
    }

    public EventsRow(String eventTitle, String eventDate, String eventDescription, String schoolID) {
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventSortableDate = "";
        this.eventDescription = eventDescription;
        this.schoolID = schoolID;
        this.key = "";
        this.isNew = false;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventSortableDate() {
        return eventSortableDate;
    }

    public void setEventSortableDate(String eventSortableDate) {
        this.eventSortableDate = eventSortableDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }
}