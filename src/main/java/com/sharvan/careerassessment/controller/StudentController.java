package com.sharvan.careerassessment.controller;

import org.springframework.web.bind.annotation.*;

import com.sharvan.careerassessment.dto.FacultyResponse;
import com.sharvan.careerassessment.service.UserService;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class StudentController {

    private final UserService userService;

    public StudentController(UserService userService) {
        this.userService = userService;
    }

    // ==========================
    // ✅ GET ASSIGNED FACULTY FOR STUDENT
    // ==========================
    @GetMapping("/{studentId}/faculty")
    public FacultyResponse getFacultyForStudent(
            @PathVariable Long studentId) {

        return userService.getFacultyForStudent(studentId);
    }
}
