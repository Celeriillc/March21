package com.celerii.celerii.models;

/**
 * Created by DELL on 8/31/2017.
 */

public class SetupSchoolListHeader {
    String noOfHits, city, state, country;

    public SetupSchoolListHeader(String noOfHits, String city, String state, String country) {
        this.noOfHits = noOfHits;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public String getNoOfHits() {
        return noOfHits;
    }

    public void setNoOfHits(String noOfHits) {
        this.noOfHits = noOfHits;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
