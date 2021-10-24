package com.celerii.celerii.models;

import android.graphics.Bitmap;

public class TeacherCreateClassPostAddImageModel {
    Bitmap bitmap;
    String uri, url;

    public TeacherCreateClassPostAddImageModel() {
        this.uri = "";
        this.url = "";
    }

    public TeacherCreateClassPostAddImageModel(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.uri = "";
        this.url = "";
    }

    public TeacherCreateClassPostAddImageModel(Bitmap bitmap, String uri, String url) {
        this.bitmap = bitmap;
        this.uri = uri;
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
