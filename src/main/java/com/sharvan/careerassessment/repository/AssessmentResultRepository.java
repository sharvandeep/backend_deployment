package com.sharvan.careerassessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import com.sharvan.careerassessment.entity.AssessmentResultEntity;

public interface AssessmentResultRepository
        extends JpaRepository<AssessmentResultEntity, Long> {

	void deleteByStudent_Id(Long studentId);
	void deleteByAssessment_Id(Long assessmentId);
    List<AssessmentResultEntity> findByStudent_Id(Long studentId);

    boolean existsByStudent_IdAndAssessment_Id(Long studentId, Long assessmentId);
    Optional<AssessmentResultEntity> findByStudent_IdAndAssessment_Id(Long studentId, Long assessmentId);
    List<AssessmentResultEntity> findByAssessment_Id(Long assessmentId);
    long countByAssessment_Id(Long assessmentId);
}
