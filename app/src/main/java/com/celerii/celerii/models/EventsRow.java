package com.celerii.celerii.models;

/**
 * Created by user on 7/27/2017.
 */

public class EventsRow {
    String eventTitle, eventDate, eventDescription, school, key;

    public EventsRow() {
    }

    public EventsRow(String eventTitle, String eventDate, String eventDescription, String school) {
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
        this.school = school;
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

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}