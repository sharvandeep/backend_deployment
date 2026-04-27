package com.sharvan.careerassessment.dto;

public class CareerPathDTO {

    private Long id;
    private String careerName;
    private String description;
    private String requiredSkills;
    private int minimumSkillPercentage;
    private String branchName;
    private Long branchId;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }
}
