package com.sharvan.careerassessment.dto;

public class AdminDashboardDTO {

    private long totalStudents;
    private long totalFaculty;
    private long totalAssessments;
    private long totalSubmissions;
    private long totalCareerPaths;

    // ===== GETTERS & SETTERS =====

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalFaculty() {
        return totalFaculty;
    }

    public void setTotalFaculty(long totalFaculty) {
        this.totalFaculty = totalFaculty;
    }

    public long getTotalAssessments() {
        return totalAssessments;
    }

    public void setTotalAssessments(long totalAssessments) {
        this.totalAssessments = totalAssessments;
    }

    public long getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(long totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public long getTotalCareerPaths() {
        return totalCareerPaths;
    }

    public void setTotalCareerPaths(long totalCareerPaths) {
        this.totalCareerPaths = totalCareerPaths;
    }
}
