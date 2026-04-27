package com.sharvan.careerassessment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.sharvan.careerassessment.dto.FacultyResponse;
import com.sharvan.careerassessment.dto.UserResponse;
import com.sharvan.careerassessment.service.UserService;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==============================
    // ✅ REGISTER
    // ==============================

    @PostMapping("/register")
    public UserResponse registerUser(@RequestBody RegistrationRequest request) {

        return userService.registerUser(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole(),
                request.getBranchName()
        );
    }

    // ==============================
    // ✅ LOGIN
    // ==============================

    @PostMapping("/login")
    public UserResponse loginUser(@RequestBody LoginRequest request) {

        return userService.loginUser(
                request.getEmail(),
                request.getPassword()
        );
    }

    // ==============================
    // ✅ GET FACULTY FOR STUDENT
    // ==============================

    @GetMapping("/students/{id}/faculty")
    public FacultyResponse getFaculty(@PathVariable Long id) {
        return userService.getFacultyForStudent(id);
    }

    // ==============================
    // ✅ GET ALL USERS
    // ==============================

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
}
