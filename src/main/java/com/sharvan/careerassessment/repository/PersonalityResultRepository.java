package com.sharvan.careerassessment.repository;

import com.sharvan.careerassessment.entity.PersonalityResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalityResultRepository extends JpaRepository<PersonalityResultEntity, Long> {
    
	void deleteByStudent_Id(Long studentId);
    List<PersonalityResultEntity> findByStudent_Id(Long studentId);
    
    Optional<PersonalityResultEntity> findByStudent_IdAndPersonalityTest_Id(Long studentId, Long testId);
    
    boolean existsByStudent_IdAndPersonalityTest_Id(Long studentId, Long testId);
    
    // Get latest result for a student
    Optional<PersonalityResultEntity> findTopByStudent_IdOrderBySubmittedAtDesc(Long studentId);
    
    long countByPersonalityTest_Id(Long testId);
}
