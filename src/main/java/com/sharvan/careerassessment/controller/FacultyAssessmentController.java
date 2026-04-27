package com.sharvan.careerassessment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.sharvan.careerassessment.dto.AssessmentRequest;
import com.sharvan.careerassessment.dto.AssessmentResponseDTO;
import com.sharvan.careerassessment.dto.FacultyResultListDTO;
import com.sharvan.careerassessment.dto.FacultyAnswerReviewDTO;
import com.sharvan.careerassessment.dto.QuestionReviewDTO;
import com.sharvan.careerassessment.entity.AssessmentEntity;
import com.sharvan.careerassessment.entity.AssessmentResultEntity;
import com.sharvan.careerassessment.entity.StudentResponseEntity;
import com.sharvan.careerassessment.entity.QuestionEntity;
import com.sharvan.careerassessment.repository.AssessmentResultRepository;
import com.sharvan.careerassessment.repository.StudentResponseRepository;
import com.sharvan.careerassessment.service.AssessmentService;

@RestController
@RequestMapping("/faculty/assessment")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class FacultyAssessmentController {

    private final AssessmentService assessmentService;
    private final AssessmentResultRepository resultRepository;
    private final StudentResponseRepository responseRepository;

    // ✅ Constructor Injection
    public FacultyAssessmentController(
            AssessmentService assessmentService,
            AssessmentResultRepository resultRepository,
            StudentResponseRepository responseRepository
    ) {
        this.assessmentService = assessmentService;
        this.resultRepository = resultRepository;
        this.responseRepository = responseRepository;
    }

    // =====================================================
    // 1️⃣ CREATE ASSESSMENT
    // =====================================================
    @PostMapping("/{facultyId}/create")
    public AssessmentResponseDTO createAssessment(
            @PathVariable Long facultyId,
            @RequestBody AssessmentRequest request
    ) {
        AssessmentEntity saved =
                assessmentService.createAssessment(facultyId, request);

        AssessmentResponseDTO dto = new AssessmentResponseDTO();
        dto.setId(saved.getId());
        dto.setTitle(saved.getTitle());
        dto.setDescription(saved.getDescription());
        dto.setCreatedAt(saved.getCreatedAt());

        return dto;
    }

    // =====================================================
    // 2️⃣ GET ALL ASSESSMENTS CREATED BY FACULTY
    // =====================================================
    @GetMapping("/{facultyId}")
    public List<AssessmentEntity> getFacultyAssessments(
            @PathVariable Long facultyId
    ) {
        return assessmentService.getFacultyAssessments(facultyId);
    }

    // =====================================================
    // 3️⃣ GET RESULTS FILTERED BY ASSESSMENT
    // =====================================================
    @GetMapping("/{assessmentId}/results")
    public List<FacultyResultListDTO> getAssessmentResults(
            @PathVariable Long assessmentId
    ) {
        List<AssessmentResultEntity> results =
                resultRepository.findByAssessment_Id(assessmentId);

        return results.stream().map(result -> {
            FacultyResultListDTO dto = new FacultyResultListDTO();
            dto.setStudentId(result.getStudent().getId());
            dto.setStudentName(result.getStudent().getName());
            dto.setTotalQuestions(result.getTotalQuestions());
            dto.setCorrectAnswers(result.getCorrectAnswers());
            dto.setPercentage(result.getPercentage());
            dto.setSubmittedAt(result.getSubmittedAt());
            return dto;
        }).toList();
    }

 // =====================================================
 // 4️⃣ GET DETAILED ANSWERS OF A STUDENT
 // =====================================================
 @GetMapping("/{assessmentId}/student/{studentId}")
 public FacultyAnswerReviewDTO reviewStudentAnswers(
         @PathVariable Long assessmentId,
         @PathVariable Long studentId
 ) {
     List<StudentResponseEntity> responses =
             responseRepository.findByAssessment_IdAndStudent_Id(assessmentId, studentId);

     int correctCount = 0;

     List<QuestionReviewDTO> questionReviews =
             responses.stream().map(response -> {
                 QuestionReviewDTO dto = new QuestionReviewDTO();
                 dto.setQuestionText(response.getQuestion().getQuestionText());
                 dto.setStudentAnswer(response.getSelectedAnswer());
                 dto.setCorrectAnswer(response.getQuestion().getCorrectAnswer());

                 boolean isCorrect = response.getSelectedAnswer() != null &&
                         response.getSelectedAnswer().equalsIgnoreCase(
                                 response.getQuestion().getCorrectAnswer());

                 dto.setMarks(isCorrect ? 1 : 0);
                 return dto;
             }).toList();

     for (QuestionReviewDTO q : questionReviews) {
         if (q.getMarks() == 1) correctCount++;
     }

     int total = questionReviews.size();
     int wrong = total - correctCount;
     double percentage = total > 0 ? (correctCount * 100.0) / total : 0.0;

     FacultyAnswerReviewDTO reviewDTO = new FacultyAnswerReviewDTO();
     reviewDTO.setQuestions(questionReviews);
     reviewDTO.setCorrectAnswers(correctCount);
     reviewDTO.setWrongAnswers(wrong);
     reviewDTO.setTotalQuestions(total);
     reviewDTO.setPercentage(percentage);

     return reviewDTO;
 }

    // =====================================================
    // 5️⃣ UPDATE ASSESSMENT DETAILS
    // =====================================================
    @PutMapping("/{assessmentId}")
    public AssessmentResponseDTO updateAssessment(
            @PathVariable Long assessmentId,
            @RequestBody com.sharvan.careerassessment.dto.UpdateAssessmentRequest request
    ) {
        AssessmentEntity updated = assessmentService.updateAssessment(assessmentId, request);
        AssessmentResponseDTO dto = new AssessmentResponseDTO();
        dto.setId(updated.getId());
        dto.setTitle(updated.getTitle());
        dto.setDescription(updated.getDescription());
        dto.setCreatedAt(updated.getCreatedAt());
        return dto;
    }

    // =====================================================
    // 6️⃣ UPDATE QUESTION DETAILS
    // =====================================================
    @PutMapping("/question/{questionId}")
    public QuestionEntity updateQuestion(
            @PathVariable Long questionId,
            @RequestBody com.sharvan.careerassessment.dto.UpdateQuestionRequest request
    ) {
        return assessmentService.updateQuestion(questionId, request);
    }

    // =====================================================
    // 7️⃣ ADD FACULTY REMARK
    // =====================================================
    @PutMapping("/result/{resultId}/remark")
    public String addFacultyRemark(
            @PathVariable Long resultId,
            @RequestBody String remark
    ) {
        AssessmentResultEntity result =
                resultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Result not found"));

        result.setFacultyRemark(remark);
        resultRepository.save(result);

        return "Remark saved successfully";
    }
}