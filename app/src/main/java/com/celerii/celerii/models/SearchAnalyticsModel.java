package com.celerii.celerii.models;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAnalyticsModel {
    String userID, accountType, accountType_platform, date, sortableDate, day, month, year, key, platform, platform_accountType, searchTerm, studentHits, schoolHits, teacherHits;
    String day_month, month_day, month_year, year_month, day_month_year, year_month_day;
    HashMap<String, Student> studentResults;
    HashMap<String, School> schoolResults;
    HashMap<String, Teacher> teacherList;

    public SearchAnalyticsModel() {
        this.userID = "";
        this.accountType = "";
        this.accountType_platform = "";
        this.date = "";
        this.sortableDate = "";
        this.day = "";
        this.month = "";
        this.year = "";
        this.key = "";
        this.platform = "";
        this.platform_accountType = "";
        this.searchTerm = "";
        this.studentHits = "0";
        this.schoolHits = "0";
        this.teacherHits = "0";
        this.day_month = "";
        this.month_day = "";
        this.month_year = "";
        this.year_month = "";
        this.day_month_year = "";
        this.year_month_day = "";
        this.studentResults = new HashMap<>();
        this.schoolResults = new HashMap<>();
        this.teacherList = new HashMap<>();
    }

    public SearchAnalyticsModel(String userID, String accountType, String date, String sortableDate, String day, String month, String year, String key, String platform, String searchTerm, String studentHits, String schoolHits, String teacherHits, HashMap<String, Student> studentResults, HashMap<String, School> schoolResults, HashMap<String, Teacher> teacherList) {
        this.userID = userID;
        this.accountType = accountType;
        this.accountType_platform = accountType + "_" + platform;
        this.date = date;
        this.sortableDate = sortableDate;
        this.day = day;
        this.month = month;
        this.year = year;
        this.key = key;
        this.platform = platform;
        this.platform_accountType = platform + "_" + accountType;
        this.searchTerm = searchTerm;
        this.studentHits = studentHits;
        this.schoolHits = schoolHits;
        this.teacherHits = teacherHits;
        this.day_month = day + "_" + month;
        this.month_day = month + "_" + day;
        this.month_year = month + "_" + year;
        this.year_month = year + "_" + month;
        this.day_month_year = day + "_" + month + "_" + year;
        this.year_month_day = year + "_" + month + "_" + day;
        this.studentResults = studentResults;
        this.schoolResults = schoolResults;
        this.teacherList = teacherList;
    }

    public SearchAnalyticsModel(String userID, String accountType, String date, String sortableDate, String day, String month, String year, String key, String platform, String searchTerm) {
        this.userID = userID;
        this.accountType = accountType;
        this.accountType_platform = accountType + "_" + platform;
        this.date = date;
        this.sortableDate = sortableDate;
        this.day = day;
        this.month = month;
        this.year = year;
        this.key = key;
        this.platform = platform;
        this.platform_accountType = platform + "_" + accountType;
        this.searchTerm = searchTerm;
        this.studentHits = "0";
        this.schoolHits = "0";
        this.teacherHits = "0";
        this.day_month = day + "_" + month;
        this.month_day = month + "_" + day;
        this.month_year = month + "_" + year;
        this.year_month = year + "_" + month;
        this.day_month_year = day + "_" + month + "_" + year;
        this.year_month_day = year + "_" + month + "_" + day;
        this.studentResults = new HashMap<>();
        this.schoolResults = new HashMap<>();
        this.teacherList = new HashMap<>();
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountType_platform() {
        return accountType_platform;
    }

    public void setAccountType_platform(String accountType_platform) {
        this.accountType_platform = accountType_platform;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSortableDate() {
        return sortableDate;
    }

    public void setSortableDate(String sortableDate) {
        this.sortableDate = sortableDate;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatform_accountType() {
        return platform_accountType;
    }

    public void setPlatform_accountType(String platform_accountType) {
        this.platform_accountType = platform_accountType;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getStudentHits() {
        return studentHits;
    }

    public void setStudentHits(String studentHits) {
        this.studentHits = studentHits;
    }

    public String getSchoolHits() {
        return schoolHits;
    }

    public void setSchoolHits(String schoolHits) {
        this.schoolHits = schoolHits;
    }

    public String getTeacherHits() {
        return teacherHits;
    }

    public void setTeacherHits(String teacherHits) {
        this.teacherHits = teacherHits;
    }

    public String getDay_month() {
        return day_month;
    }

    public void setDay_month(String day_month) {
        this.day_month = day_month;
    }

    public String getMonth_day() {
        return month_day;
    }

    public void setMonth_day(String month_day) {
        this.month_day = month_day;
    }

    public String getMonth_year() {
        return month_year;
    }

    public void setMonth_year(String month_year) {
        this.month_year = month_year;
    }

    public String getYear_month() {
        return year_month;
    }

    public void setYear_month(String year_month) {
        this.year_month = year_month;
    }

    public String getDay_month_year() {
        return day_month_year;
    }

    public void setDay_month_year(String day_month_year) {
        this.day_month_year = day_month_year;
    }

    public String getYear_month_day() {
        return year_month_day;
    }

    public void setYear_month_day(String year_month_day) {
        this.year_month_day = year_month_day;
    }

    public HashMap<String, Student> getStudentResults() {
        return studentResults;
    }

    public void setStudentResults(HashMap<String, Student> studentResults) {
        this.studentResults = studentResults;
    }

    public HashMap<String, School> getSchoolResults() {
        return schoolResults;
    }

    public void setSchoolResults(HashMap<String, School> schoolResults) {
        this.schoolResults = schoolResults;
    }

    public HashMap<String, Teacher> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(HashMap<String, Teacher> teacherList) {
        this.teacherList = teacherList;
    }
}
