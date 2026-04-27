package com.sharvan.careerassessment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharvan.careerassessment.entity.BranchSkillEntity;

public interface BranchSkillRepository
        extends JpaRepository<BranchSkillEntity, Long> {

    List<BranchSkillEntity> findByBranch_Id(Long branchId);
}