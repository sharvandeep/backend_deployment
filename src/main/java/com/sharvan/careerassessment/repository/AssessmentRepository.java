package com.sharvan.careerassessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sharvan.careerassessment.entity.AssessmentEntity;
import java.util.List;

public interface AssessmentRepository 
        extends JpaRepository<AssessmentEntity, Long> {

    List<AssessmentEntity> findByBranchId(Long branchId);
    List<AssessmentEntity> findByFaculty_Id(Long facultyId);

}
