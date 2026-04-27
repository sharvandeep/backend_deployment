package com.sharvan.careerassessment.dto;

import java.time.LocalDateTime;

public class ResultHistoryDTO {

    private String assessmentTitle;
    private int totalQuestions;
    private int correctAnswers;
    private double percentage;
    private LocalDateTime submittedAt;
    private Long assessmentId;
    private String facultyRemark;

    

    

    // ==========================
    // GETTERS
    // ==========================

    public String getAssessmentTitle() {
        return assessmentTitle;
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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    
    public String getFacultyRemark() {
        return facultyRemark;
    }
    

    // ==========================
    // SETTERS
    // ==========================

    public void setAssessmentTitle(String assessmentTitle) {
        this.assessmentTitle = assessmentTitle;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

	public Long getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(Long assessmentId) {
		this.assessmentId = assessmentId;
	}
	
	public void setFacultyRemark(String facultyRemark) {
        this.facultyRemark = facultyRemark;
    }
}
