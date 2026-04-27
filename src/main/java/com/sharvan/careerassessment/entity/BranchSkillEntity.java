package com.sharvan.careerassessment.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "branch_skill")
public class BranchSkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skillName;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private BranchEntity branch;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public String getSkillName() {
        return skillName;
    }

    public BranchEntity getBranch() {
        return branch;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setBranch(BranchEntity branch) {
        this.branch = branch;
    }
}