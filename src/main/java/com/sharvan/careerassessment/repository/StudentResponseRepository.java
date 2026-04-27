package com.sharvan.careerassessment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharvan.careerassessment.entity.StudentResponseEntity;

public interface StudentResponseRepository
        extends JpaRepository<StudentResponseEntity, Long> {
	void deleteByStudent_Id(Long studentId);

	// 🔥 NEW
	void deleteByAssessment_Id(Long assessmentId);
	
    // Already used for detailed review
    List<StudentResponseEntity> findByAssessment_IdAndStudent_Id(
            Long assessmentId,
            Long studentId
    );

    // For skill analysis
    List<StudentResponseEntity> findByStudent_Id(Long studentId);

    // For admin - delete responses by assessment
    List<StudentResponseEntity> findByAssessment_Id(Long assessmentId);
    
    
    
}


