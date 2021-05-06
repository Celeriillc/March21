package com.celerii.celerii.models;

import com.celerii.celerii.helperClasses.Date;

public class QuestionModel {
    String questionID, question, answer, selectedAnswer, optionA, optionB, optionC, optionD, date, sortableDate;

    public QuestionModel() {
        this.questionID = "";
        this.question = "";
        this.answer = "";
        this.selectedAnswer = "";
        this.optionA = "";
        this.optionB = "";
        this.optionC = "";
        this.optionD = "";
        this.date = "";
        this.sortableDate = "";
    }

    public QuestionModel(String question, String answer, String optionA, String optionB, String optionC, String optionD, String date) {
        this.questionID = "";
        this.question = question;
        this.answer = answer;
        this.selectedAnswer = "";
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.date = date;
        this.sortableDate = Date.convertToSortableDate(date);
    }

    public QuestionModel(String question, String answer, String selectedAnswer, String optionA, String optionB, String optionC, String optionD, String date) {
        this.questionID = "";
        this.question = question;
        this.answer = answer;
        this.selectedAnswer = selectedAnswer;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.date = date;
        this.sortableDate = Date.convertToSortableDate(date);
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
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
}
