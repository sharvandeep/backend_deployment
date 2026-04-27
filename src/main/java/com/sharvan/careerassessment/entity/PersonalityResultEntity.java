package com.sharvan.careerassessment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Personality Result Entity
 * Stores student's personality profile with scores for each Big Five trait
 * Scores are percentages (0-100)
 */
@Entity
@Table(name = "personality_results")
public class PersonalityResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    @ManyToOne
    @JoinColumn(name = "personality_test_id", nullable = false)
    private PersonalityTestEntity personalityTest;

    // Big Five trait scores (0-100 percentage)
    private double opennessScore;
    private double conscientiousnessScore;
    private double extraversionScore;
    private double agreeablenessScore;
    private double neuroticismScore;

    // Dominant trait (highest score)
    @Enumerated(EnumType.STRING)
    private PersonalityTrait dominantTrait;

    // Secondary trait (second highest)
    @Enumerated(EnumType.STRING)
    private PersonalityTrait secondaryTrait;

    private LocalDateTime submittedAt;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getStudent() {
        return student;
    }

    public void setStudent(UserEntity student) {
        this.student = student;
    }

    public PersonalityTestEntity getPersonalityTest() {
        return personalityTest;
    }

    public void setPersonalityTest(PersonalityTestEntity personalityTest) {
        this.personalityTest = personalityTest;
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

    public PersonalityTrait getDominantTrait() {
        return dominantTrait;
    }

    public void setDominantTrait(PersonalityTrait dominantTrait) {
        this.dominantTrait = dominantTrait;
    }

    public PersonalityTrait getSecondaryTrait() {
        return secondaryTrait;
    }

    public void setSecondaryTrait(PersonalityTrait secondaryTrait) {
        this.secondaryTrait = secondaryTrait;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    // Helper method to get score by trait
    public double getScoreByTrait(PersonalityTrait trait) {
        return switch (trait) {
            case OPENNESS -> opennessScore;
            case CONSCIENTIOUSNESS -> conscientiousnessScore;
            case EXTRAVERSION -> extraversionScore;
            case AGREEABLENESS -> agreeablenessScore;
            case NEUROTICISM -> neuroticismScore;
        };
    }

    // Helper method to set score by trait
    public void setScoreByTrait(PersonalityTrait trait, double score) {
        switch (trait) {
            case OPENNESS -> this.opennessScore = score;
            case CONSCIENTIOUSNESS -> this.conscientiousnessScore = score;
            case EXTRAVERSION -> this.extraversionScore = score;
            case AGREEABLENESS -> this.agreeablenessScore = score;
            case NEUROTICISM -> this.neuroticismScore = score;
        }
    }
}
