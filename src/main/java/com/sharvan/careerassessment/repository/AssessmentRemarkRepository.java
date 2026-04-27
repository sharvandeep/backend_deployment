package com.sharvan.careerassessment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharvan.careerassessment.entity.AssessmentRemarkEntity;

@Repository
public interface AssessmentRemarkRepository
        extends JpaRepository<AssessmentRemarkEntity, Long> {

    List<AssessmentRemarkEntity> findByStudent_Id(Long studentId);

    List<AssessmentRemarkEntity> findByStudent_IdAndIsReadFalse(Long studentId);
    
    Optional<AssessmentRemarkEntity> findByAssessment_IdAndStudent_Id(Long assessmentId, Long studentId);
    
    List<AssessmentRemarkEntity> findByFaculty_IdAndAssessment_Id(Long facultyId, Long assessmentId);
}