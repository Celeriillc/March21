package com.celerii.celerii.models;

/**
 * Created by DELL on 9/1/2017.
 */

public class SearchHistoryHeader {
    String header;
    boolean loading;

    public SearchHistoryHeader(String header, boolean loading) {
        this.header = header;
        this.loading = loading;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}
