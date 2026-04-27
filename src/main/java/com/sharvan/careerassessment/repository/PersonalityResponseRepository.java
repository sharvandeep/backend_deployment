package com.sharvan.careerassessment.repository;

import com.sharvan.careerassessment.entity.PersonalityResponseEntity;
import com.sharvan.careerassessment.entity.PersonalityTrait;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalityResponseRepository extends JpaRepository<PersonalityResponseEntity, Long> {
    
	void deleteByStudent_Id(Long studentId);
    List<PersonalityResponseEntity> findByStudent_IdAndPersonalityTest_Id(Long studentId, Long testId);
    
    List<PersonalityResponseEntity> findByStudent_Id(Long studentId);
    
    boolean existsByStudent_IdAndPersonalityTest_Id(Long studentId, Long testId);
    
    void deleteByStudent_IdAndPersonalityTest_Id(Long studentId, Long testId);
}
