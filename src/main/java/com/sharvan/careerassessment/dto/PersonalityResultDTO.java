package com.sharvan.careerassessment.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for personality test results
 */
public class PersonalityResultDTO {
    
    private Long id;
    private Long testId;
    private String testTitle;
    private LocalDateTime submittedAt;
    
    // Trait scores (0-100)
    private double opennessScore;
    private double conscientiousnessScore;
    private double extraversionScore;
    private double agreeablenessScore;
    private double neuroticismScore;
    
    // Dominant traits
    private String dominantTrait;
    private String dominantTraitDescription;
    private String secondaryTrait;
    private String secondaryTraitDescription;
    
    // Trait breakdown for chart display
    private List<TraitScore> traitScores;

    // ===== INNER CLASS =====
    
    public static class TraitScore {
        private String trait;
        private String displayName;
        private double score;
        private String level;  // HIGH, MODERATE, LOW
        private String description;

        public TraitScore() {}

        public TraitScore(String trait, String displayName, double score, String level, String description) {
            this.trait = trait;
            this.displayName = displayName;
            this.score = score;
            this.level = level;
            this.description = description;
        }

        public String getTrait() {
            return trait;
        }

        public void setTrait(String trait) {
            this.trait = trait;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    // ===== CONSTRUCTORS =====
    
    public PersonalityResultDTO() {}

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestTitle() {
        return testTitle;
    }

    public void setTestTitle(String testTitle) {
        this.testTitle = testTitle;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public double getOpennessScore() {
        return opennessScore;
    }

    public void setOpennessScore(double opennessScore) {
        this.opennessScore = opennessScore;
    }

    public double getConscientiousnessScore() {
        return conscientiousnessScore;
    }

    public void setConscientiousnessScore(double conscientiousnessScore) {
        this.conscientiousnessScore = conscientiousnessScore;
    }

    public double getExtraversionScore() {
        return extraversionScore;
    }

    public void setExtraversionScore(double extraversionScore) {
        this.extraversionScore = extraversionScore;
    }

    public double getAgreeablenessScore() {
        return agreeablenessScore;
    }

    public void setAgreeablenessScore(double agreeablenessScore) {
        this.agreeablenessScore = agreeablenessScore;
    }

    public double getNeuroticismScore() {
        return neuroticismScore;
    }

    public void setNeuroticismScore(double neuroticismScore) {
        this.neuroticismScore = neuroticismScore;
    }

    public String getDominantTrait() {
        return dominantTrait;
    }

    public void setDominantTrait(String dominantTrait) {
        this.dominantTrait = dominantTrait;
    }

    public String getDominantTraitDescription() {
        return dominantTraitDescription;
    }

    public void setDominantTraitDescription(String dominantTraitDescription) {
        this.dominantTraitDescription = dominantTraitDescription;
    }

    public String getSecondaryTrait() {
        return secondaryTrait;
    }

    public void setSecondaryTrait(String secondaryTrait) {
        this.secondaryTrait = secondaryTrait;
    }

    public String getSecondaryTraitDescription() {
        return secondaryTraitDescription;
    }

    public void setSecondaryTraitDescription(String secondaryTraitDescription) {
        this.secondaryTraitDescription = secondaryTraitDescription;
    }

    public List<TraitScore> getTraitScores() {
        return traitScores;
    }

    public void setTraitScores(List<TraitScore> traitScores) {
        this.traitScores = traitScores;
    }
}
