package com.sharvan.careerassessment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sharvan.careerassessment.dto.*;
import com.sharvan.careerassessment.entity.*;
import com.sharvan.careerassessment.repository.*;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;
    private final AssessmentResultRepository resultRepository;
    private final CareerPathRepository careerPathRepository;
    private final BranchRepository branchRepository;
    private final StudentResponseRepository responseRepository;
    private final PersonalityResponseRepository personalityResponseRepository;
    private final PersonalityResultRepository personalityResultRepository;

    public AdminService(
            UserRepository userRepository,
            AssessmentRepository assessmentRepository,
            AssessmentResultRepository resultRepository,
            CareerPathRepository careerPathRepository,
            BranchRepository branchRepository,
            StudentResponseRepository responseRepository,
            PersonalityResponseRepository personalityResponseRepository,
            PersonalityResultRepository personalityResultRepository
    ) {
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
        this.resultRepository = resultRepository;
        this.careerPathRepository = careerPathRepository;
        this.branchRepository = branchRepository;
        this.responseRepository = responseRepository;
        this.personalityResponseRepository = personalityResponseRepository;
        this.personalityResultRepository = personalityResultRepository;
    }

    // =====================================================
    // 1️⃣ DASHBOARD STATISTICS
    // =====================================================
    public AdminDashboardDTO getDashboardStats() {

        AdminDashboardDTO dto = new AdminDashboardDTO();

        dto.setTotalStudents(userRepository.countByRole(Role.STUDENT));
        dto.setTotalFaculty(userRepository.countByRole(Role.FACULTY));
        dto.setTotalAssessments(assessmentRepository.count());
        dto.setTotalSubmissions(resultRepository.count());
        dto.setTotalCareerPaths(careerPathRepository.count());

        return dto;
    }

    // =====================================================
    // 2️⃣ GET ALL USERS
    // =====================================================
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // =====================================================
    // 3️⃣ DELETE USER (SAFE VERSION)
    // =====================================================
    @Transactional
    public void deleteUser(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot delete admin user");
        }

        // ==========================
        // STUDENT CLEANUP
        // ==========================
        if (user.getRole() == Role.STUDENT) {

            resultRepository.deleteByStudent_Id(userId);
            responseRepository.deleteByStudent_Id(userId);
            personalityResponseRepository.deleteByStudent_Id(userId);
            personalityResultRepository.deleteByStudent_Id(userId);
        }

        // ==========================
        // FACULTY CLEANUP
        // ==========================
        if (user.getRole() == Role.FACULTY) {

            // Unassign students
            List<UserEntity> students = user.getAssignedStudents();
            if (students != null) {
                for (UserEntity student : students) {
                    student.setAssignedFaculty(null);
                    userRepository.save(student);
                }
            }

            // Delete faculty assessments safely
            List<AssessmentEntity> assessments =
                    assessmentRepository.findByFaculty_Id(userId);

            for (AssessmentEntity assessment : assessments) {

                resultRepository.deleteByAssessment_Id(assessment.getId());
                responseRepository.deleteByAssessment_Id(assessment.getId());

                assessmentRepository.delete(assessment);
            }
        }

        userRepository.delete(user);
    }

    // =====================================================
    // 4️⃣ GET ALL ASSESSMENTS
    // =====================================================
    public List<AssessmentAdminDTO> getAllAssessments() {

        return assessmentRepository.findAll()
                .stream()
                .map(assessment -> {

                    AssessmentAdminDTO dto = new AssessmentAdminDTO();
                    dto.setId(assessment.getId());
                    dto.setTitle(assessment.getTitle());
                    dto.setDescription(assessment.getDescription());
                    dto.setCreatedAt(assessment.getCreatedAt());
                    dto.setFacultyName(assessment.getFaculty().getName());
                    dto.setBranchName(assessment.getBranch().getName());
                    dto.setQuestionCount(
                            assessment.getQuestions() != null
                                    ? assessment.getQuestions().size()
                                    : 0
                    );
                    dto.setSubmissionCount(
                            resultRepository.countByAssessment_Id(assessment.getId())
                    );

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // =====================================================
    // 5️⃣ DELETE ASSESSMENT
    // =====================================================
    @Transactional
    public void deleteAssessment(Long assessmentId) {

        AssessmentEntity assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        resultRepository.deleteByAssessment_Id(assessmentId);
        responseRepository.deleteByAssessment_Id(assessmentId);

        assessmentRepository.delete(assessment);
    }

    // =====================================================
    // 6️⃣ CREATE CAREER PATH
    // =====================================================
    public CareerPathDTO createCareerPath(CareerPathRequest request) {

        BranchEntity branch = null;
        if (request.getBranchId() != null) {
            branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
        }

        CareerPathEntity entity = new CareerPathEntity();
        entity.setCareerName(request.getCareerName());
        entity.setDescription(request.getDescription());
        entity.setRequiredSkills(request.getRequiredSkills());
        entity.setMinimumSkillPercentage(request.getMinimumSkillPercentage());
        entity.setBranch(branch);

        CareerPathEntity saved = careerPathRepository.save(entity);

        return mapToCareerPathDTO(saved);
    }

    // =====================================================
    // 7️⃣ GET ALL CAREER PATHS
    // =====================================================
    public List<CareerPathDTO> getAllCareerPaths() {

        return careerPathRepository.findAll()
                .stream()
                .map(this::mapToCareerPathDTO)
                .collect(Collectors.toList());
    }

    // =====================================================
    // 8️⃣ UPDATE CAREER PATH
    // =====================================================
    public CareerPathDTO updateCareerPath(Long id, CareerPathRequest request) {

        CareerPathEntity entity = careerPathRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Career path not found"));

        entity.setCareerName(request.getCareerName());
        entity.setDescription(request.getDescription());
        entity.setRequiredSkills(request.getRequiredSkills());
        entity.setMinimumSkillPercentage(request.getMinimumSkillPercentage());

        if (request.getBranchId() != null) {
            BranchEntity branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            entity.setBranch(branch);
        }

        CareerPathEntity saved = careerPathRepository.save(entity);
        return mapToCareerPathDTO(saved);
    }

    // =====================================================
    // 9️⃣ DELETE CAREER PATH
    // =====================================================
    public void deleteCareerPath(Long id) {
        careerPathRepository.deleteById(id);
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================
    private UserResponse mapToUserResponse(UserEntity user) {

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUserCode(user.getUserCode());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setBranch(user.getBranch() != null ? user.getBranch().getName() : "N/A");
        response.setBranchId(user.getBranch() != null ? user.getBranch().getId() : null);

        return response;
    }

    private CareerPathDTO mapToCareerPathDTO(CareerPathEntity entity) {

        CareerPathDTO dto = new CareerPathDTO();
        dto.setId(entity.getId());
        dto.setCareerName(entity.getCareerName());
        dto.setDescription(entity.getDescription());
        dto.setRequiredSkills(entity.getRequiredSkills());
        dto.setMinimumSkillPercentage(entity.getMinimumSkillPercentage());
        dto.setBranchName(entity.getBranch() != null ? entity.getBranch().getName() : "All Branches");
        dto.setBranchId(entity.getBranch() != null ? entity.getBranch().getId() : null);

        return dto;
    }
}