package com.celerii.celerii.models;

/**
 * Created by DELL on 10/2/2017.
 */

public class AttendanceStatusModel {
    String attendanceStatus, remark;

    public AttendanceStatusModel() {
        this.attendanceStatus = "";
        this.remark = "";
    }

    public AttendanceStatusModel(String attendanceStatus, String remark) {
        this.attendanceStatus = attendanceStatus;
        this.remark = remark;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
