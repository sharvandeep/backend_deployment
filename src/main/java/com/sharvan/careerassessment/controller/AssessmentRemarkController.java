package com.sharvan.careerassessment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sharvan.careerassessment.entity.AssessmentRemarkEntity;

@RestController
@RequestMapping("/api/remarks")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AssessmentRemarkController {

    private final AssessmentRemarkService remarkService;

    public AssessmentRemarkController(AssessmentRemarkService remarkService) {
        this.remarkService = remarkService;
    }

    // =====================================================
    // 1️⃣ ADD REMARK (FACULTY ONLY)
    // =====================================================
    @PostMapping("/faculty/add")
    public ResponseEntity<String> addRemark(
            @RequestBody AddRemarkRequest request
    ) {
        try {
            remarkService.addRemark(
                    request.getFacultyId(),
                    request.getStudentId(),
                    request.getAssessmentId(),
                    request.getRemark()
            );
            return ResponseEntity.ok("Remark added successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =====================================================
    // 2️⃣ GET ALL REMARKS FOR A SPECIFIC ASSESSMENT
    // =====================================================
    @GetMapping("/assessment/{assessmentId}/student/{studentId}")
    public ResponseEntity<AssessmentRemarkEntity> getRemarkForAssessment(
            @PathVariable Long assessmentId,
            @PathVariable Long studentId
    ) {
        try {
            AssessmentRemarkEntity remark = remarkService.getRemarkByAssessmentAndStudent(
                    assessmentId,
                    studentId
            );
            if (remark != null) {
                return ResponseEntity.ok(remark);
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // =====================================================
    // 3️⃣ GET ALL REMARKS FOR A STUDENT
    // =====================================================
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AssessmentRemarkEntity>> getStudentRemarks(
            @PathVariable Long studentId
    ) {
        try {
            List<AssessmentRemarkEntity> remarks = remarkService.getRemarksByStudent(studentId);
            return ResponseEntity.ok(remarks);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // =====================================================
    // 4️⃣ GET UNREAD REMARKS COUNT
    // =====================================================
    @GetMapping("/student/{studentId}/unread-count")
    public ResponseEntity<Long> getUnreadRemarkCount(
            @PathVariable Long studentId
    ) {
        try {
            long count = remarkService.getUnreadRemarkCount(studentId);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // =====================================================
    // 5️⃣ MARK ALL REMARKS AS READ
    // =====================================================
    @PutMapping("/student/{studentId}/mark-read")
    public ResponseEntity<String> markRemarksAsRead(
            @PathVariable Long studentId
    ) {
        try {
            remarkService.markRemarksAsRead(studentId);
            return ResponseEntity.ok("All remarks marked as read");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =====================================================
    // 6️⃣ MARK SPECIFIC REMARK AS READ
    // =====================================================
    @PutMapping("/{remarkId}/mark-read")
    public ResponseEntity<String> markRemarkAsRead(
            @PathVariable Long remarkId
    ) {
        try {
            remarkService.markRemarkAsRead(remarkId);
            return ResponseEntity.ok("Remark marked as read");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =====================================================
    // 7️⃣ GET FACULTY'S REMARKS FOR ASSESSMENT
    // =====================================================
    @GetMapping("/faculty/{facultyId}/assessment/{assessmentId}")
    public ResponseEntity<List<AssessmentRemarkEntity>> getFacultyRemarksForAssessment(
            @PathVariable Long facultyId,
            @PathVariable Long assessmentId
    ) {
        try {
            List<AssessmentRemarkEntity> remarks = remarkService.getRemarksByFacultyAndAssessment(
                    facultyId,
                    assessmentId
            );
            return ResponseEntity.ok(remarks);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // =====================================================
    // 8️⃣ DELETE REMARK (FACULTY ONLY)
    // =====================================================
    @DeleteMapping("/{remarkId}")
    public ResponseEntity<String> deleteRemark(
            @PathVariable Long remarkId
    ) {
        try {
            remarkService.deleteRemark(remarkId);
            return ResponseEntity.ok("Remark deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

// Request class for adding remark
class AddRemarkRequest {
    private Long facultyId;
    private Long studentId;
    private Long assessmentId;
    private String remark;

    public Long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
