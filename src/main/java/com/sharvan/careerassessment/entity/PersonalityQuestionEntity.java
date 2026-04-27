package com.sharvan.careerassessment.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Personality Question Entity
 * Questions use 5-point Likert scale (1-5)
 * 1 = Strongly Disagree, 2 = Disagree, 3 = Neutral, 4 = Agree, 5 = Strongly Agree
 */
@Entity
@Table(name = "personality_questions")
public class PersonalityQuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String questionText;

    // Which Big Five trait this question measures
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonalityTrait trait;

    // Direction: true = positive (high score = high trait)
    //            false = reverse scored (high score = low trait)
    private boolean positiveDirection = true;

    // Question order within the test
    private int questionOrder;

    @ManyToOne
    @JoinColumn(name = "personality_test_id")
    @JsonBackReference
    private PersonalityTestEntity personalityTest;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public PersonalityTrait getTrait() {
        return trait;
    }

    public void setTrait(PersonalityTrait trait) {
        this.trait = trait;
    }

    public boolean isPositiveDirection() {
        return positiveDirection;
    }

    public void setPositiveDirection(boolean positiveDirection) {
        this.positiveDirection = positiveDirection;
    }

    public int getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(int questionOrder) {
        this.questionOrder = questionOrder;
    }

    public PersonalityTestEntity getPersonalityTest() {
        return personalityTest;
    }

    public void setPersonalityTest(PersonalityTestEntity personalityTest) {
        this.personalityTest = personalityTest;
    }
}
