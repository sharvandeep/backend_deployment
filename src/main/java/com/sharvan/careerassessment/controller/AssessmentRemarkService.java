package com.sharvan.careerassessment.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sharvan.careerassessment.entity.AssessmentEntity;
import com.sharvan.careerassessment.entity.AssessmentRemarkEntity;
import com.sharvan.careerassessment.entity.UserEntity;
import com.sharvan.careerassessment.repository.AssessmentRemarkRepository;
import com.sharvan.careerassessment.repository.AssessmentRepository;
import com.sharvan.careerassessment.repository.UserRepository;
import com.sharvan.careerassessment.service.NotificationService;

@Service("assessmentRemarkControllerService")
public class AssessmentRemarkService {

    private final AssessmentRemarkRepository remarkRepository;
    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;
    private final NotificationService notificationService;

    public AssessmentRemarkService(
            AssessmentRemarkRepository remarkRepository,
            UserRepository userRepository,
            AssessmentRepository assessmentRepository,
            NotificationService notificationService
    ) {
        this.remarkRepository = remarkRepository;
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
        this.notificationService = notificationService;
    }

    // =====================================================
    // 1️⃣ ADD REMARK (FACULTY)
    // =====================================================
    public void addRemark(Long facultyId,
                          Long studentId,
                          Long assessmentId,
                          String remarkText) {

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

        // 📢 Create notification for student
        try {
            notificationService.createNotification(
                    student.getId(),
                    faculty.getId(),
                    "New Remark Added",
                    faculty.getName() + " has added a remark to your assessment: " + assessment.getTitle(),
                    "REMARK",
                    "/student/review/" + assessment.getId() + "/" + student.getId(),
                    remark.getId()
            );
        } catch (Exception e) {
            System.err.println("Failed to create notification: " + e.getMessage());
        }
    }

    // =====================================================
    // 2️⃣ GET ALL REMARKS FOR STUDENT
    // =====================================================
    public List<AssessmentRemarkEntity> getRemarksByStudent(Long studentId) {
        return remarkRepository.findByStudent_Id(studentId);
    }

    // =====================================================
    // 3️⃣ GET UNREAD REMARK COUNT (🔔 NOTIFICATION)
    // =====================================================
    public long getUnreadRemarkCount(Long studentId) {
        return remarkRepository
                .findByStudent_IdAndIsReadFalse(studentId)
                .size();
    }

    // =====================================================
    // 4️⃣ MARK ALL REMARKS AS READ
    // =====================================================
    public void markRemarksAsRead(Long studentId) {

        List<AssessmentRemarkEntity> remarks =
                remarkRepository.findByStudent_IdAndIsReadFalse(studentId);

        for (AssessmentRemarkEntity remark : remarks) {
            remark.setRead(true);
        }

        remarkRepository.saveAll(remarks);
    }

    // =====================================================
    // 5️⃣ MARK SPECIFIC REMARK AS READ
    // =====================================================
    public void markRemarkAsRead(Long remarkId) {
        AssessmentRemarkEntity remark = remarkRepository.findById(remarkId)
                .orElseThrow(() -> new RuntimeException("Remark not found"));
        
        remark.setRead(true);
        remarkRepository.save(remark);
    }

    // =====================================================
    // 6️⃣ GET REMARK FOR ASSESSMENT AND STUDENT
    // =====================================================
    public AssessmentRemarkEntity getRemarkByAssessmentAndStudent(Long assessmentId, Long studentId) {
        return remarkRepository.findByAssessment_IdAndStudent_Id(assessmentId, studentId)
                .orElse(null);
    }

    // =====================================================
    // 7️⃣ GET FACULTY'S REMARKS FOR ASSESSMENT
    // =====================================================
    public List<AssessmentRemarkEntity> getRemarksByFacultyAndAssessment(Long facultyId, Long assessmentId) {
        return remarkRepository.findByFaculty_IdAndAssessment_Id(facultyId, assessmentId);
    }

    // =====================================================
    // 8️⃣ DELETE REMARK
    // =====================================================
    public void deleteRemark(Long remarkId) {
        AssessmentRemarkEntity remark = remarkRepository.findById(remarkId)
                .orElseThrow(() -> new RuntimeException("Remark not found"));
        
        remarkRepository.delete(remark);
    }
}