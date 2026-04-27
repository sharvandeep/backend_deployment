package com.sharvan.careerassessment.dto;

public class AssessmentResultResponse {

    private int totalQuestions;
    private int correctAnswers;
    private double percentage;

    public AssessmentResultResponse(int totalQuestions, int correctAnswers, double percentage) {
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.percentage = percentage;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public double getPercentage() {
        return percentage;
    }
}
