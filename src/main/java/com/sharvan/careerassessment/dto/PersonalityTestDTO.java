package com.sharvan.careerassessment.dto;

/**
 * DTO for listing personality tests
 */
public class PersonalityTestDTO {
    
    private Long id;
    private String title;
    private String description;
    private int totalQuestions;
    private int estimatedMinutes;
    private boolean isActive;
    private boolean alreadyTaken;  // For student view

    // ===== CONSTRUCTORS =====
    
    public PersonalityTestDTO() {}

    public PersonalityTestDTO(Long id, String title, String description, 
                              int totalQuestions, int estimatedMinutes, 
                              boolean isActive, boolean alreadyTaken) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.totalQuestions = totalQuestions;
        this.estimatedMinutes = estimatedMinutes;
        this.isActive = isActive;
        this.alreadyTaken = alreadyTaken;
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isAlreadyTaken() {
        return alreadyTaken;
    }

    public void setAlreadyTaken(boolean alreadyTaken) {
        this.alreadyTaken = alreadyTaken;
    }
}
