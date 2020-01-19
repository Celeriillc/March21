package com.celerii.celerii.models;

/**
 * Created by user on 7/18/2017.
 */

public class PerformanceFutureModel {

    String subject;
    int averageScore, forecastScore;

    public PerformanceFutureModel() {
    }

    public PerformanceFutureModel(String subject, int averageScore, int forecastScore) {
        this.subject = subject;
        this.averageScore = averageScore;
        this.forecastScore = forecastScore;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(int averageScore) {
        this.averageScore = averageScore;
    }

    public int getForecastScore() {
        return forecastScore;
    }

    public void setForecastScore(int forecastScore) {
        this.forecastScore = forecastScore;
    }
}
