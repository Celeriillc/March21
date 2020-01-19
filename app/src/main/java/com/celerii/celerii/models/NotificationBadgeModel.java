package com.celerii.celerii.models;

/**
 * Created by DELL on 12/1/2018.
 */

public class NotificationBadgeModel {
    Boolean status;
    int number;

    public NotificationBadgeModel() {
    }

    public NotificationBadgeModel(Boolean status, int number) {
        this.status = status;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
