package com.sharvan.careerassessment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sharvan.careerassessment.entity.QuestionEntity;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    List<QuestionEntity> findByAssessment_Id(Long assessmentId);
}
