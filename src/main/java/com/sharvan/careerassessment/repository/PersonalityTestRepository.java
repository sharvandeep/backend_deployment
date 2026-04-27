package com.sharvan.careerassessment.repository;

import com.sharvan.careerassessment.entity.PersonalityTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalityTestRepository extends JpaRepository<PersonalityTestEntity, Long> {
    
    List<PersonalityTestEntity> findByIsActiveTrue();
    
    List<PersonalityTestEntity> findByIsActiveTrueOrderByCreatedAtDesc();
}
