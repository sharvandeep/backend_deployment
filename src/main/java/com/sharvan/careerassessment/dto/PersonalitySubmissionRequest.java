package com.sharvan.careerassessment.dto;

import java.util.List;

/**
 * Request DTO for submitting personality test responses
 */
public class PersonalitySubmissionRequest {
    
    private Long testId;
    private Long studentId;
    private List<PersonalityAnswerRequest> answers;

    // ===== INNER CLASS =====
    
    public static class PersonalityAnswerRequest {
        private Long questionId;
        private int response;  // 1-5 Likert scale

        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public int getResponse() {
            return response;
        }

        public void setResponse(int response) {
            this.response = response;
        }
    }

    // ===== GETTERS & SETTERS =====

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public List<PersonalityAnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<PersonalityAnswerRequest> answers) {
        this.answers = answers;
    }
}
