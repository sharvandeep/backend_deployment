package com.sharvan.careerassessment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Personality Response Entity
 * Stores individual question responses (Likert scale 1-5)
 */
@Entity
@Table(name = "personality_responses")
public class PersonalityResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private PersonalityQuestionEntity question;

    @ManyToOne
    @JoinColumn(name = "personality_test_id", nullable = false)
    private PersonalityTestEntity personalityTest;

    // Likert scale response (1-5)
    // 1 = Strongly Disagree
    // 2 = Disagree
    // 3 = Neutral
    // 4 = Agree
    // 5 = Strongly Agree
    private int response;

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

    public PersonalityQuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(PersonalityQuestionEntity question) {
        this.question = question;
    }

    public PersonalityTestEntity getPersonalityTest() {
        return personalityTest;
    }

    public void setPersonalityTest(PersonalityTestEntity personalityTest) {
        this.personalityTest = personalityTest;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
