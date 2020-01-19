package com.celerii.celerii.models;

/**
 * Created by DELL on 11/6/2017.
 */

public class InboxHeader {
    String header, body;

    public InboxHeader() {
    }

    public InboxHeader(String body) {
        this.body = body;
    }

    public InboxHeader(String header, String body) {
        this.header = header;
        this.body = body;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
