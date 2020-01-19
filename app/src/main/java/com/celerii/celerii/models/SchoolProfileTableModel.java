package com.celerii.celerii.models;

/**
 * Created by DELL on 4/28/2019.
 */

public class SchoolProfileTableModel {
    String mainText, subText;

    public SchoolProfileTableModel() {
    }

    public SchoolProfileTableModel(String mainText, String subText) {
        this.mainText = mainText;
        this.subText = subText;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }
}
