package com.sharvan.careerassessment.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "career_paths")
public class CareerPathEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String careerName;

    private String description;

    // Comma-separated skills required (e.g., "PROGRAMMING,APTITUDE")
    @Column(length = 500)
    private String requiredSkills;

    // Minimum percentage required in those skills
    private int minimumSkillPercentage;

    // Ideal personality traits for this career (comma-separated)
    // e.g., "CONSCIENTIOUSNESS,OPENNESS" 
    @Column(length = 200)
    private String idealPersonalityTraits;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private BranchEntity branch;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCareerName() {
        return careerName;
    }

    public void setCareerName(String careerName) {
        this.careerName = careerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public int getMinimumSkillPercentage() {
        return minimumSkillPercentage;
    }

    public void setMinimumSkillPercentage(int minimumSkillPercentage) {
        this.minimumSkillPercentage = minimumSkillPercentage;
    }

    public String getIdealPersonalityTraits() {
        return idealPersonalityTraits;
    }

    public void setIdealPersonalityTraits(String idealPersonalityTraits) {
        this.idealPersonalityTraits = idealPersonalityTraits;
    }

    public BranchEntity getBranch() {
        return branch;
    }

    public void setBranch(BranchEntity branch) {
        this.branch = branch;
    }
}
