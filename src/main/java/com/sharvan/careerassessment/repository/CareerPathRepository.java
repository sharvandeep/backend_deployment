package com.sharvan.careerassessment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharvan.careerassessment.entity.CareerPathEntity;

public interface CareerPathRepository extends JpaRepository<CareerPathEntity, Long> {

    List<CareerPathEntity> findByBranch_Id(Long branchId);
    
    List<CareerPathEntity> findByRequiredSkillsContaining(String skill);
}
