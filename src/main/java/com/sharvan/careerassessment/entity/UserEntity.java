package com.sharvan.careerassessment.entity;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Custom visible ID (F1, S1 etc.)
    @Column(unique = true)
    private String userCode;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // ===== BRANCH =====
    @ManyToOne
    @JoinColumn(name = "branch_id")
    private BranchEntity branch;

    // ===== STUDENT → FACULTY (Many students → One faculty) =====
    @ManyToOne
    @JoinColumn(name = "faculty_id")
    @JsonBackReference
    private UserEntity assignedFaculty;

    @OneToMany(mappedBy = "assignedFaculty")
    @JsonManagedReference
    private List<UserEntity> assignedStudents;


    // =======================
    // GETTERS & SETTERS
    // =======================

    public Long getId() {
        return id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    // Later we will hash password
    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public BranchEntity getBranch() {
        return branch;
    }

    public void setBranch(BranchEntity branch) {
        this.branch = branch;
    }

    public UserEntity getAssignedFaculty() {
        return assignedFaculty;
    }

    public void setAssignedFaculty(UserEntity assignedFaculty) {
        this.assignedFaculty = assignedFaculty;
    }

    public List<UserEntity> getAssignedStudents() {
        return assignedStudents;
    }

    public void setAssignedStudents(List<UserEntity> assignedStudents) {
        this.assignedStudents = assignedStudents;
    }
}
