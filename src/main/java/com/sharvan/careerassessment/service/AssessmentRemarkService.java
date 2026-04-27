package com.sharvan.careerassessment.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.sharvan.careerassessment.entity.*;
import com.sharvan.careerassessment.entity.AssessmentRemarkEntity;
import com.sharvan.careerassessment.repository.*;

@Service
public class AssessmentRemarkService {

    private final AssessmentRemarkRepository remarkRepository;
    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;

    public AssessmentRemarkService(
            AssessmentRemarkRepository remarkRepository,
            UserRepository userRepository,
            AssessmentRepository assessmentRepository
    ) {
        this.remarkRepository = remarkRepository;
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
    }

    // =====================================
    // ADD REMARK
    // =====================================
    public void addRemark(Long facultyId, Long studentId,
                          Long assessmentId, String remarkText) {

        UserEntity faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        AssessmentEntity assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        AssessmentRemarkEntity remark = new AssessmentRemarkEntity();
        remark.setFaculty(faculty);
        remark.setStudent(student);
        remark.setAssessment(assessment);
        remark.setRemark(remarkText);
        remark.setCreatedAt(LocalDateTime.now());
        remark.setRead(false);

        remarkRepository.save(remark);
    }
 // GET unread count
    public long getUnreadRemarkCount(Long studentId) {
        return remarkRepository
                .findByStudent_IdAndIsReadFalse(studentId)
                .size();
    }

    // MARK AS READ
    public void markRemarksAsRead(Long studentId) {
        var remarks = remarkRepository
                .findByStudent_IdAndIsReadFalse(studentId);

        for (AssessmentRemarkEntity remark : remarks) {
            remark.setRead(true);
        }

        remarkRepository.saveAll(remarks);
    }
}