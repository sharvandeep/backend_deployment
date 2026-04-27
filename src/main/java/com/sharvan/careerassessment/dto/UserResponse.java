package com.sharvan.careerassessment.dto;

public class UserResponse {

    private Long id;
    private String userCode;
    private String name;
    private String email;
    private String role;
    private String branch;
    private Long branchId;

    // 🔥 VERY IMPORTANT (default constructor)
    public UserResponse() {
    }

    // ================= GETTERS =================

    public Long getId() { return id; }

    public String getUserCode() { return userCode; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public String getRole() { return role; }

    public String getBranch() { return branch; }

    public Long getBranchId() { return branchId; }

    // ================= SETTERS =================

    public void setId(Long id) { this.id = id; }
 
    public void setUserCode(String userCode) { this.userCode = userCode; }

    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setRole(String role) { this.role = role; }

    public void setBranch(String branch) { this.branch = branch; }

    public void setBranchId(Long branchId) { this.branchId = branchId; }
}