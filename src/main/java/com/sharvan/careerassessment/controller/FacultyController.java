package com.sharvan.careerassessment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.sharvan.careerassessment.dto.UserResponse;
import com.sharvan.careerassessment.service.UserService;

@RestController
@RequestMapping("/faculty")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class FacultyController {

    private final UserService userService;

    public FacultyController(UserService userService) {
        this.userService = userService;
    }

    // ==========================
    // ✅ GET STUDENTS ASSIGNED TO FACULTY
    // ==========================
    @GetMapping("/{facultyId}/students")
    public List<UserResponse> getStudentsByFaculty(
            @PathVariable Long facultyId) {

        return userService.getStudentsByFaculty(facultyId);
    }
}
