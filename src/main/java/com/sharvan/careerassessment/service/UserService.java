package com.sharvan.careerassessment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sharvan.careerassessment.dto.FacultyResponse;
import com.sharvan.careerassessment.dto.UserResponse;
import com.sharvan.careerassessment.entity.*;
import com.sharvan.careerassessment.repository.*;



@Service
public class UserService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired   // ✅ ADD THIS
    public UserService(UserRepository userRepository,
                       BranchRepository branchRepository,
                       BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // ==========================================
    // REGISTER USER
    // ==========================================

    public UserResponse registerUser(String name,
                                     String email,
                                     String password,
                                     String role,
                                     String branchName) {

        BranchEntity branch = branchRepository
                .findByName(branchName)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        Role userRole;

        try {
            userRole = Role.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid role");
        }

        UserEntity user = new UserEntity();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(userRole);
        user.setBranch(branch);

        // Generate userCode
        long count = userRepository.countByRole(userRole);
        String prefix = userRole == Role.FACULTY ? "F" : "S";
        user.setUserCode(prefix + (count + 1));

        // FACULTY
        if (userRole == Role.FACULTY) {
            UserEntity saved = userRepository.save(user);
            return mapToUserResponse(saved);
        }

        // STUDENT
        List<UserEntity> faculties =
                userRepository.findByRoleAndBranch(Role.FACULTY, branch);

        if (faculties.isEmpty()) {
        	throw new ResponseStatusException(
        	        HttpStatus.BAD_REQUEST,
        	        "Currently no faculty available in this branch. Please contact admin."
        	);
        }

        UserEntity assignedFaculty = null;

        for (UserEntity faculty : faculties) {
            long studentCount =
                    userRepository.countByAssignedFaculty(faculty);

            if (studentCount < 7) {
                assignedFaculty = faculty;
                break;
            }
        }

        if (assignedFaculty == null) {
            throw new RuntimeException(
                    "All faculty in this branch have reached max student limit");
        }

        user.setAssignedFaculty(assignedFaculty);

        UserEntity saved = userRepository.save(user);

        return mapToUserResponse(saved);
    }

    // ==========================================
    // LOGIN USER
    // ==========================================
    public UserResponse loginUser(String email, String password) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return mapToUserResponse(user);
    }
    // ==========================================
    // GET STUDENTS BY FACULTY
    // ==========================================

    public List<UserResponse> getStudentsByFaculty(Long facultyId) {

        UserEntity faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        if (faculty.getRole() != Role.FACULTY) {
            throw new RuntimeException("User is not a faculty");
        }

        return faculty.getAssignedStudents()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // GET FACULTY FOR STUDENT
    // ==========================================

    public FacultyResponse getFacultyForStudent(Long studentId) {

        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("User is not a student");
        }

        UserEntity faculty = student.getAssignedFaculty();

        if (faculty == null || faculty.getBranch() == null) {
            return new FacultyResponse(
                faculty != null ? faculty.getId() : null,
                faculty != null ? faculty.getUserCode() : "N/A",
                faculty != null ? faculty.getName() : "N/A",
                faculty != null ? faculty.getEmail() : "N/A",
                "N/A"
            );
        }

        return new FacultyResponse(
                faculty.getId(),
                faculty.getUserCode(),
                faculty.getName(),
                faculty.getEmail(),
                faculty.getBranch().getName()
        );
    }

    // ==========================================
    // GET ALL USERS
    // ==========================================

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // COMMON DTO MAPPER
    // ==========================================

    private UserResponse mapToUserResponse(UserEntity user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setUserCode(user.getUserCode());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        
        if (user.getBranch() != null) {
            response.setBranch(user.getBranch().getName());
            response.setBranchId(user.getBranch().getId());
        } else {
            response.setBranch("N/A");
            response.setBranchId(null);
        }

        return response;
    }
}
