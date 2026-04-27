package com.sharvan.careerassessment.dto;

/**
 * DTO for personality questions (excludes trait info for students)
 */
public class PersonalityQuestionDTO {
    
    private Long id;
    private String questionText;
    private int questionOrder;

    // ===== CONSTRUCTORS =====
    
    public PersonalityQuestionDTO() {}

    public PersonalityQuestionDTO(Long id, String questionText, int questionOrder) {
        this.id = id;
        this.questionText = questionText;
        this.questionOrder = questionOrder;
    }

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

    public int getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(int questionOrder) {
        this.questionOrder = questionOrder;
    }
}
