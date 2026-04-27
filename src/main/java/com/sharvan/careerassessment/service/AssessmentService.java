package com.sharvan.careerassessment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sharvan.careerassessment.dto.AssessmentRequest;
import com.sharvan.careerassessment.dto.QuestionRequest;
import com.sharvan.careerassessment.entity.AssessmentEntity;
import com.sharvan.careerassessment.entity.QuestionEntity;
import com.sharvan.careerassessment.entity.UserEntity;
import com.sharvan.careerassessment.entity.Role;
import com.sharvan.careerassessment.repository.AssessmentRepository;
import com.sharvan.careerassessment.repository.QuestionRepository;
import com.sharvan.careerassessment.repository.UserRepository;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AssessmentService(AssessmentRepository assessmentRepository,
                             QuestionRepository questionRepository,
                             UserRepository userRepository,
                             NotificationService notificationService) {
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // =====================================================
    // 1️⃣ Create Assessment WITH Questions (Atomic Transaction)
    // =====================================================
    @Transactional
    public AssessmentEntity createAssessment(Long facultyId,
                                             AssessmentRequest request) {

        // 🔍 Validate Faculty
        UserEntity faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        if (!faculty.getRole().name().equals("FACULTY")) {
            throw new RuntimeException("Only faculty can create assessments");
        }

        // 📝 Create Assessment
        AssessmentEntity assessment = new AssessmentEntity();
        assessment.setTitle(request.getTitle());
        assessment.setDescription(request.getDescription());
        assessment.setCreatedAt(LocalDateTime.now());
        assessment.setFaculty(faculty);
        assessment.setBranch(faculty.getBranch());

        AssessmentEntity savedAssessment =
                assessmentRepository.save(assessment);

        // 🧠 Save Questions (WITH SKILL CATEGORY)
        if (request.getQuestions() != null &&
            !request.getQuestions().isEmpty()) {

            for (QuestionRequest q : request.getQuestions()) {
            	
            	
                QuestionEntity question = new QuestionEntity();

                question.setQuestionText(q.getQuestionText());
                question.setOptiona(q.getOptionA());
                question.setOptionb(q.getOptionB());
                question.setOptionc(q.getOptionC());
                question.setOptiond(q.getOptionD());
                question.setCorrectAnswer(q.getCorrectAnswer());
                question.setSkillCategory(q.getSkillCategory());

                // 🔥 NEW: Save skill category

                question.setAssessment(savedAssessment);

                questionRepository.save(question);
            }
        }

        // 🔔 Send notifications to all students in the branch
        if (savedAssessment.getBranch() != null) {
            List<UserEntity> students = userRepository.findByRoleAndBranch(Role.STUDENT, savedAssessment.getBranch());
            
            for (UserEntity student : students) {
                try {
                    notificationService.createNotification(
                        student.getId(),                                  // recipient
                        facultyId,                                        // triggered by faculty
                        "New Assessment Available",                        // title
                        "A new assessment \"" + savedAssessment.getTitle() + "\" has been created by " + faculty.getName(),
                        "ASSESSMENT_AVAILABLE",                           // notification type
                        "/student/assessments",                           // action URL
                        savedAssessment.getId()                          // related entity ID
                    );
                } catch (Exception e) {
                    // Log error but don't fail the assessment creation
                    System.err.println("Failed to create notification for student " + student.getId() + ": " + e.getMessage());
                }
            }
        }

        return savedAssessment;
    }

    // =====================================================
    // 2️⃣ Get All Assessments Created by Faculty
    // =====================================================
    public List<AssessmentEntity> getFacultyAssessments(Long facultyId) {

        // 🔍 Validate Faculty
        UserEntity faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        if (!faculty.getRole().name().equals("FACULTY")) {
            throw new RuntimeException("Access denied");
        }

        return assessmentRepository.findByFaculty_Id(facultyId);
    }

        // =====================================================
        // 3️⃣ Update Assessment Details
        // =====================================================
        @Transactional
        public AssessmentEntity updateAssessment(Long assessmentId, com.sharvan.careerassessment.dto.UpdateAssessmentRequest request) {
            AssessmentEntity assessment = assessmentRepository.findById(assessmentId)
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));
            if (request.getTitle() != null) assessment.setTitle(request.getTitle());
            if (request.getDescription() != null) assessment.setDescription(request.getDescription());
            if (request.getBranchId() != null) {
                // Optionally update branch
                // BranchEntity branch = branchRepository.findById(request.getBranchId()).orElse(null);
                // if (branch != null) assessment.setBranch(branch);
            }
            return assessmentRepository.save(assessment);
        }

        // =====================================================
        // 4️⃣ Update Question Details
        // =====================================================
        @Transactional
        public QuestionEntity updateQuestion(Long questionId, com.sharvan.careerassessment.dto.UpdateQuestionRequest request) {
            QuestionEntity question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found"));
            if (request.getQuestionText() != null) question.setQuestionText(request.getQuestionText());
            if (request.getOptionA() != null) question.setOptiona(request.getOptionA());
            if (request.getOptionB() != null) question.setOptionb(request.getOptionB());
            if (request.getOptionC() != null) question.setOptionc(request.getOptionC());
            if (request.getOptionD() != null) question.setOptiond(request.getOptionD());
            if (request.getCorrectAnswer() != null) question.setCorrectAnswer(request.getCorrectAnswer());
            if (request.getSkillCategory() != null) question.setSkillCategory(request.getSkillCategory());
            return questionRepository.save(question);
        }
}