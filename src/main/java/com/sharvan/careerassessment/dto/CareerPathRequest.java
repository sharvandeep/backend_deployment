package com.sharvan.careerassessment.dto;

public class CareerPathRequest {

    private String careerName;
    private String description;
    private String requiredSkills;
    private int minimumSkillPercentage;
    private Long branchId;

    // ===== GETTERS & SETTERS =====

    public String getCareerName() {
        return careerName;
    }

    public void setCareerName(String careerName) {
        this.careerName = careerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public int getMinimumSkillPercentage() {
        return minimumSkillPercentage;
    }

    public void setMinimumSkillPercentage(int minimumSkillPercentage) {
        this.minimumSkillPercentage = minimumSkillPercentage;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }
}
