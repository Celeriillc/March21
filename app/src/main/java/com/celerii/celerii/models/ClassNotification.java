package com.celerii.celerii.models;

/**
 * Created by user on 7/3/2017.
 */

public class ClassNotification {
    String notification, picUrl, time, notificationtypeUrl;

    public ClassNotification(String notification, String picUrl, String time, String notificationtypeUrl) {
        this.notification = notification;
        this.picUrl = picUrl;
        this.time = time;
        this.notificationtypeUrl = notificationtypeUrl;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNotificationtypeUrl() {
        return notificationtypeUrl;
    }

    public void setNotificationtypeUrl(String notificationtypeUrl) {
        this.notificationtypeUrl = notificationtypeUrl;
    }
}
