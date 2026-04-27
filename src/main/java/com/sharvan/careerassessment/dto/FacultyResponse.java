package com.sharvan.careerassessment.dto;

public class FacultyResponse {

    private Long facultyId;
    private String facultyCode;
    private String name;
    private String email;
    private String branch;

    public FacultyResponse(Long facultyId, String facultyCode,
                           String name, String email, String branch) {
        this.facultyId = facultyId;
        this.facultyCode = facultyCode;
        this.name = name;
        this.email = email;
        this.branch = branch;
    }

    public Long getFacultyId() { return facultyId; }
    public String getFacultyCode() { return facultyCode; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getBranch() { return branch; }
}
