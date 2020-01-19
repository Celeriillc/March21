package com.celerii.celerii.models;

/**
 * Created by DELL on 5/6/2019.
 */

public class TeacherRewardModel {
    String reward, point;
    boolean isCustom;

    public TeacherRewardModel() {
    }

    public TeacherRewardModel(String reward, String point, boolean isCustom) {
        this.reward = reward;
        this.point = point;
        this.isCustom = isCustom;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }
}
