package com.sharvan.careerassessment.dto;

public class CareerRecommendationDTO {

    private Long id;
    private String careerName;
    private String description;
    private String requiredSkills;
    private int matchPercentage;        // Combined match (skills + personality)
    private int skillMatchPercentage;   // Skills only match
    private int personalityMatchPercentage; // Personality only match
    private String matchLevel;          // STRONG, MODERATE, WEAK
    private String explination;

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

    public int getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(int matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public int getSkillMatchPercentage() {
        return skillMatchPercentage;
    }

    public void setSkillMatchPercentage(int skillMatchPercentage) {
        this.skillMatchPercentage = skillMatchPercentage;
    }

    public int getPersonalityMatchPercentage() {
        return personalityMatchPercentage;
    }

    public void setPersonalityMatchPercentage(int personalityMatchPercentage) {
        this.personalityMatchPercentage = personalityMatchPercentage;
    }

    public String getMatchLevel() {
        return matchLevel;
    }

    public void setMatchLevel(String matchLevel) {
        this.matchLevel = matchLevel;
    }

	public void setExplanation(String explanation) {
		// TODO Auto-generated method stub
		
	}
}
