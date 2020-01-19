package com.celerii.celerii.models;

/**
 * Created by DELL on 9/1/2017.
 */

public class SearchHistoryRow {
    String entityId, entityName, entityAddress, entityType, time;

    public SearchHistoryRow() {
    }

    public SearchHistoryRow(String entityId, String entityName, String entityAddress, String entityType) {
        this.entityId = entityId;
        this.entityName = entityName;
        this.entityAddress = entityAddress;
        this.entityType = entityType;
    }

    public SearchHistoryRow(String entityId, String entityName, String entityAddress, String entityType, String time) {
        this.entityId = entityId;
        this.entityName = entityName;
        this.entityAddress = entityAddress;
        this.entityType = entityType;
        this.time = time;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityAddress() {
        return entityAddress;
    }

    public void setEntityAddress(String entityAddress) {
        this.entityAddress = entityAddress;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
