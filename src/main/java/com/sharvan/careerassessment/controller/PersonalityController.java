package com.sharvan.careerassessment.controller;

import com.sharvan.careerassessment.dto.*;
import com.sharvan.careerassessment.service.PersonalityService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Personality Tests
 * Endpoints for students to take personality tests and view results
 */
@RestController
@RequestMapping("/personality")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class PersonalityController {

    private final PersonalityService personalityService;

    public PersonalityController(PersonalityService personalityService) {
        this.personalityService = personalityService;
    }

    // ==========================================
    // GET AVAILABLE TESTS FOR STUDENT
    // ==========================================
    
    @GetMapping("/tests/{studentId}")
    public ResponseEntity<List<PersonalityTestDTO>> getAvailableTests(@PathVariable Long studentId) {
        List<PersonalityTestDTO> tests = personalityService.getAvailableTests(studentId);
        return ResponseEntity.ok(tests);
    }

    // ==========================================
    // GET TEST QUESTIONS
    // ==========================================
    
    @GetMapping("/test/{testId}/questions")
    public ResponseEntity<List<PersonalityQuestionDTO>> getTestQuestions(@PathVariable Long testId) {
        List<PersonalityQuestionDTO> questions = personalityService.getTestQuestions(testId);
        return ResponseEntity.ok(questions);
    }

    // ==========================================
    // SUBMIT PERSONALITY TEST
    // ==========================================
    
    @PostMapping("/submit")
    public ResponseEntity<PersonalityResultDTO> submitTest(@RequestBody PersonalitySubmissionRequest request) {
        PersonalityResultDTO result = personalityService.submitTest(request);
        return ResponseEntity.ok(result);
    }

    // ==========================================
    // GET STUDENT'S ALL RESULTS
    // ==========================================
    
    @GetMapping("/results/{studentId}")
    public ResponseEntity<List<PersonalityResultDTO>> getStudentResults(@PathVariable Long studentId) {
        List<PersonalityResultDTO> results = personalityService.getStudentResults(studentId);
        return ResponseEntity.ok(results);
    }

    // ==========================================
    // GET LATEST RESULT FOR STUDENT
    // ==========================================
    
    @GetMapping("/results/{studentId}/latest")
    public ResponseEntity<PersonalityResultDTO> getLatestResult(@PathVariable Long studentId) {
        PersonalityResultDTO result = personalityService.getLatestResult(studentId);
        if (result == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    // ==========================================
    // GET SPECIFIC RESULT BY ID
    // ==========================================
    
    @GetMapping("/result/{resultId}")
    public ResponseEntity<PersonalityResultDTO> getResult(@PathVariable Long resultId) {
        PersonalityResultDTO result = personalityService.getResult(resultId);
        return ResponseEntity.ok(result);
    }

    // ==========================================
    // EXPORT SPECIFIC RESULT AS PDF
    // ==========================================
    @GetMapping("/result/{resultId}/export")
    public ResponseEntity<byte[]> exportResultPdf(@PathVariable Long resultId) {
        PersonalityService.PersonalityPdfReport report = personalityService.exportResultPdf(resultId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.fileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(report.bytes());
    }

    // ==========================================
    // ADMIN: GET ALL TESTS
    // ==========================================
    
    @GetMapping("/admin/tests")
    public ResponseEntity<List<PersonalityTestDTO>> getAllTests() {
        List<PersonalityTestDTO> tests = personalityService.getAllTests();
        return ResponseEntity.ok(tests);
    }
}
