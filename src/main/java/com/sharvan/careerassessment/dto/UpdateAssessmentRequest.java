package com.sharvan.careerassessment.dto;

public class UpdateAssessmentRequest {
    private String title;
    private String description;
    private Long branchId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
}
