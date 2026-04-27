package com.sharvan.careerassessment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.sharvan.careerassessment.dto.*;
import com.sharvan.careerassessment.service.AdminService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // =====================================================
    // 1️⃣ DASHBOARD STATISTICS
    // =====================================================
    @GetMapping("/dashboard")
    public AdminDashboardDTO getDashboardStats() {
        return adminService.getDashboardStats();
    }

    // =====================================================
    // 2️⃣ USER MANAGEMENT
    // =====================================================
    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return "User deleted successfully";
    }

    // =====================================================
    // 3️⃣ ASSESSMENT MANAGEMENT
    // =====================================================
    @GetMapping("/assessments")
    public List<AssessmentAdminDTO> getAllAssessments() {
        return adminService.getAllAssessments();
    }

    @DeleteMapping("/assessments/{assessmentId}")
    public String deleteAssessment(@PathVariable Long assessmentId) {
        adminService.deleteAssessment(assessmentId);
        return "Assessment deleted successfully";
    }

    // =====================================================
    // 4️⃣ CAREER PATH MANAGEMENT
    // =====================================================
    @GetMapping("/career-paths")
    public List<CareerPathDTO> getAllCareerPaths() {
        return adminService.getAllCareerPaths();
    }

    @PostMapping("/career-paths")
    public CareerPathDTO createCareerPath(@RequestBody CareerPathRequest request) {
        return adminService.createCareerPath(request);
    }

    @PutMapping("/career-paths/{id}")
    public CareerPathDTO updateCareerPath(
            @PathVariable Long id,
            @RequestBody CareerPathRequest request
    ) {
        return adminService.updateCareerPath(id, request);
    }

    @DeleteMapping("/career-paths/{id}")
    public String deleteCareerPath(@PathVariable Long id) {
        adminService.deleteCareerPath(id);
        return "Career path deleted successfully";
    }
}
