package com.sharvan.careerassessment.repository;

import com.sharvan.careerassessment.entity.PersonalityQuestionEntity;
import com.sharvan.careerassessment.entity.PersonalityTrait;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalityQuestionRepository extends JpaRepository<PersonalityQuestionEntity, Long> {
    
    List<PersonalityQuestionEntity> findByPersonalityTest_IdOrderByQuestionOrderAsc(Long testId);
    
    List<PersonalityQuestionEntity> findByPersonalityTest_IdAndTrait(Long testId, PersonalityTrait trait);
    
    long countByPersonalityTest_Id(Long testId);
}
