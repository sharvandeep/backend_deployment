package com.sharvan.careerassessment.dto;

public class StudentResponse {

    private Long studentId;
    private String studentCode;
    private String name;
    private String email;

    public StudentResponse(Long studentId, String studentCode,
                           String name, String email) {
        this.studentId = studentId;
        this.studentCode = studentCode;
        this.name = name;
        this.email = email;
    }

    public Long getStudentId() { return studentId; }
    public String getStudentCode() { return studentCode; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
