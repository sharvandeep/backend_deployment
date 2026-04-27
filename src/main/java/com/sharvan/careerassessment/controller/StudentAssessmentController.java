package com.sharvan.careerassessment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sharvan.careerassessment.dto.*;
import com.sharvan.careerassessment.entity.*;
import com.sharvan.careerassessment.repository.*;

@RestController
@RequestMapping("/student")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class StudentAssessmentController {

    @Autowired
    private StudentResponseRepository responseRepository;

    @Autowired
    private AssessmentResultRepository resultRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private com.sharvan.careerassessment.service.CareerRecommendationService careerRecommendationService;

    @Autowired
    private com.sharvan.careerassessment.service.NotificationService notificationService;

    // =====================================================
    // 1️⃣ Get Questions by Assessment (SAFE DTO)
    // =====================================================
    @GetMapping("/assessment/{assessmentId}/questions")
    public List<QuestionResponseDTO> getQuestions(
            @PathVariable Long assessmentId
    ) {

        AssessmentEntity assessment = assessmentRepository
                .findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        return assessment.getQuestions().stream().map(question -> {
            QuestionResponseDTO dto = new QuestionResponseDTO();
            dto.setId(question.getId());
            dto.setQuestionText(question.getQuestionText());
            dto.setOptiona(question.getOptiona());
            dto.setOptionb(question.getOptionb());
            dto.setOptionc(question.getOptionc());
            dto.setOptiond(question.getOptiond());
            return dto;
        }).toList();
    }

    // =====================================================
    // 2️⃣ Submit Full Assessment (WITH SKILL TRACKING)
    // =====================================================
    @PostMapping("/submit")
    public AssessmentResultResponse submitAssessment(
            @RequestBody AssessmentSubmissionRequest request
    ) {

        UserEntity student = userRepository
                .findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        AssessmentEntity assessment = assessmentRepository
                .findById(request.getAssessmentId())
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        boolean alreadySubmitted =
                resultRepository.existsByStudent_IdAndAssessment_Id(
                        student.getId(),
                        assessment.getId()
                );

        if (alreadySubmitted) {
            throw new RuntimeException("You have already attempted this assessment.");
        }

        Map<String, Integer> skillTotal = new HashMap<>();
        Map<String, Integer> skillCorrect = new HashMap<>();

        int correctCount = 0;
        int totalQuestions = request.getAnswers().size();

        for (AnswerRequest answer : request.getAnswers()) {

            QuestionEntity question = questionRepository
                    .findById(answer.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            String skill = question.getSkillCategory();
            skillTotal.put(skill, skillTotal.getOrDefault(skill, 0) + 1);

            boolean isCorrect = question.getCorrectAnswer()
                    .equalsIgnoreCase(answer.getSelectedAnswer());

            if (isCorrect) {
                correctCount++;
                skillCorrect.put(skill, skillCorrect.getOrDefault(skill, 0) + 1);
            }

            StudentResponseEntity response = new StudentResponseEntity();
            response.setStudent(student);
            response.setAssessment(assessment);
            response.setQuestion(question);
            response.setSelectedAnswer(answer.getSelectedAnswer());
            response.setSubmittedAt(LocalDateTime.now());

            responseRepository.save(response);
        }

        double percentage =
                totalQuestions == 0 ? 0 :
                        ((double) correctCount / totalQuestions) * 100;

        AssessmentResultEntity result = new AssessmentResultEntity();
        result.setStudent(student);
        result.setAssessment(assessment);
        result.setTotalQuestions(totalQuestions);
        result.setCorrectAnswers(correctCount);
        result.setPercentage(percentage);
        result.setSubmittedAt(LocalDateTime.now());

        resultRepository.save(result);

        // 📢 Create notification for assigned faculty
        if (student.getAssignedFaculty() != null) {
            try {
                notificationService.createNotification(
                        student.getAssignedFaculty().getId(),
                        student.getId(),
                        "Assessment Submitted",
                        student.getName() + " has submitted the assessment: " + assessment.getTitle(),
                        "SUBMISSION",
                        "/faculty/results?assessmentId=" + assessment.getId(),
                        result.getId()
                );
            } catch (Exception e) {
                // Log error but don't fail the submission
                System.err.println("Failed to create notification: " + e.getMessage());
            }
        }

        return new AssessmentResultResponse(
                totalQuestions,
                correctCount,
                percentage
        );
    }

    // =====================================================
    // 3️⃣ Student Result History
    // =====================================================
    @GetMapping("/results/{studentId}")
    public List<ResultHistoryDTO> getStudentResults(
            @PathVariable Long studentId
    ) {

        List<AssessmentResultEntity> results =
                resultRepository.findByStudent_Id(studentId);
        

        return results.stream().map(result -> {
        	
            ResultHistoryDTO dto = new ResultHistoryDTO();
            dto.setAssessmentId(result.getAssessment().getId());
            dto.setAssessmentTitle(result.getAssessment().getTitle());
            dto.setTotalQuestions(result.getTotalQuestions());
            dto.setCorrectAnswers(result.getCorrectAnswers());
            dto.setPercentage(result.getPercentage());
            dto.setSubmittedAt(result.getSubmittedAt());
            dto.setFacultyRemark(result.getFacultyRemark());
            return dto;
        }).toList();
    }

    // =====================================================
    // 3️⃣1️⃣ Export a single assessment result report (PDF)
    // =====================================================
    @GetMapping("/results/{studentId}/assessment/{assessmentId}/export")
    public ResponseEntity<byte[]> exportStudentResultReport(
            @PathVariable Long studentId,
            @PathVariable Long assessmentId
    ) {
        AssessmentResultEntity result = resultRepository
                .findByStudent_IdAndAssessment_Id(studentId, assessmentId)
                .orElseThrow(() -> new RuntimeException("Result not found for this student and assessment"));

        List<StudentResponseEntity> responses = responseRepository
                .findByAssessment_IdAndStudent_Id(assessmentId, studentId);

        byte[] fileBytes = buildPdfReport(result, responses);
        String safeStudentName = sanitizeFilePart(result.getStudent().getName());
        String safeAssessmentTitle = sanitizeFilePart(result.getAssessment().getTitle());
        String fileName = safeAssessmentTitle + "_" + safeStudentName + "_report.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(fileBytes);
    }

    // =====================================================
    // 4️⃣ Skill Analysis
    // =====================================================
    @GetMapping("/skill-analysis/{studentId}")
    public List<SkillAnalysisDTO> getSkillAnalysis(
            @PathVariable Long studentId
    ) {

        List<StudentResponseEntity> responses =
                responseRepository.findByStudent_Id(studentId);

        Map<String, Integer> total = new HashMap<>();
        Map<String, Integer> correct = new HashMap<>();

        for (StudentResponseEntity response : responses) {

            String skill = response.getQuestion().getSkillCategory();

            total.put(skill, total.getOrDefault(skill, 0) + 1);

            if (response.getQuestion().getCorrectAnswer()
                    .equalsIgnoreCase(response.getSelectedAnswer())) {

                correct.put(skill, correct.getOrDefault(skill, 0) + 1);
            }
        }

        return total.keySet().stream().map(skill -> {

            SkillAnalysisDTO dto = new SkillAnalysisDTO();
            dto.setSkill(skill);
            dto.setTotal(total.get(skill));
            dto.setCorrect(correct.getOrDefault(skill, 0));

            double percent =
                    (double) correct.getOrDefault(skill, 0)
                            / total.get(skill) * 100;

            dto.setPercentage(percent);

            return dto;

        }).toList();
    }

    // =====================================================
    // 5️⃣ Branch Based Assessments
    // =====================================================
    @GetMapping("/assessments/{studentId}")
    public List<AssessmentListDTO> getAvailableAssessments(
            @PathVariable Long studentId
    ) {

        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Long branchId = student.getBranch().getId();

        List<AssessmentEntity> assessments =
                assessmentRepository.findByBranchId(branchId);

        return assessments.stream().map(assessment -> {

            AssessmentListDTO dto = new AssessmentListDTO();
            dto.setId(assessment.getId());
            dto.setTitle(assessment.getTitle());
            dto.setDescription(assessment.getDescription());
            dto.setCreatedAt(assessment.getCreatedAt());
            dto.setFacultyName(assessment.getFaculty().getName());

            return dto;

        }).toList();
    }

    // =====================================================
    // 6️⃣ CAREER RECOMMENDATIONS
    // =====================================================
    @GetMapping("/career-recommendations/{studentId}")
    public List<CareerRecommendationDTO> getCareerRecommendations(
            @PathVariable Long studentId
    ) {
        return careerRecommendationService.getRecommendations(studentId);
    }

        private byte[] buildPdfReport(AssessmentResultEntity result, List<StudentResponseEntity> responses) {
                try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                        Document document = new Document(PageSize.A4, 28, 28, 28, 28);
                        PdfWriter.getInstance(document, output);
                        document.open();

                        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
                        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
                        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
                        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

                        Paragraph title = new Paragraph("Assessment Performance Report", titleFont);
                        title.setAlignment(Paragraph.ALIGN_CENTER);
                        document.add(title);

                        Paragraph subtitle = new Paragraph(
                                        "Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")),
                                        subtitleFont
                        );
                        subtitle.setAlignment(Paragraph.ALIGN_CENTER);
                        subtitle.setSpacingAfter(14f);
                        document.add(subtitle);

                        PdfPTable summaryTable = new PdfPTable(new float[]{1.2f, 2.8f});
                        summaryTable.setWidthPercentage(100);
                        summaryTable.setSpacingAfter(14f);

                        addSummaryRow(summaryTable, "Student", result.getStudent().getName(), bodyFont);
                        addSummaryRow(summaryTable, "Assessment", result.getAssessment().getTitle(), bodyFont);
                        addSummaryRow(summaryTable, "Submitted At", String.valueOf(result.getSubmittedAt()), bodyFont);
                        addSummaryRow(summaryTable, "Score", String.format(Locale.US, "%.2f%%", result.getPercentage()), bodyFont);
                        addSummaryRow(summaryTable, "Correct / Total", result.getCorrectAnswers() + " / " + result.getTotalQuestions(), bodyFont);
                        addSummaryRow(summaryTable, "Faculty Remark", result.getFacultyRemark() != null ? result.getFacultyRemark() : "No remark", bodyFont);
                        document.add(summaryTable);

                        Paragraph questionTitle = new Paragraph("Question-wise Analysis", sectionFont);
                        questionTitle.setSpacingAfter(8f);
                        document.add(questionTitle);

                        PdfPTable detailTable = new PdfPTable(new float[]{0.6f, 3.2f, 1.4f, 1.3f, 1.3f, 0.9f});
                        detailTable.setWidthPercentage(100);

                        addHeaderCell(detailTable, "#", tableHeaderFont);
                        addHeaderCell(detailTable, "Question", tableHeaderFont);
                        addHeaderCell(detailTable, "Skill", tableHeaderFont);
                        addHeaderCell(detailTable, "Your Ans", tableHeaderFont);
                        addHeaderCell(detailTable, "Correct Ans", tableHeaderFont);
                        addHeaderCell(detailTable, "Status", tableHeaderFont);

                        int index = 1;
                        for (StudentResponseEntity response : responses) {
                                QuestionEntity question = response.getQuestion();
                                String selected = response.getSelectedAnswer();
                                String correct = question.getCorrectAnswer();
                                boolean isCorrect = correct != null && correct.equalsIgnoreCase(selected);

                                addBodyCell(detailTable, String.valueOf(index++), bodyFont);
                                addBodyCell(detailTable, safeText(question.getQuestionText()), bodyFont);
                                addBodyCell(detailTable, safeText(question.getSkillCategory()), bodyFont);
                                addBodyCell(detailTable, safeText(selected), bodyFont);
                                addBodyCell(detailTable, safeText(correct), bodyFont);
                                addBodyCell(detailTable, isCorrect ? "Correct" : "Wrong", bodyFont);
                        }

                        document.add(detailTable);
                        document.close();
                        return output.toByteArray();
                } catch (DocumentException e) {
                        throw new RuntimeException("Failed to generate PDF report", e);
                } catch (Exception e) {
                        throw new RuntimeException("Unexpected error while generating PDF report", e);
                }
        }

        private void addSummaryRow(PdfPTable table, String label, String value, Font bodyFont) {
                PdfPCell labelCell = new PdfPCell(new Phrase(label, bodyFont));
                labelCell.setBorder(Rectangle.BOX);
                labelCell.setPadding(8f);
                labelCell.setBackgroundColor(new java.awt.Color(232, 240, 254));
                table.addCell(labelCell);

                PdfPCell valueCell = new PdfPCell(new Phrase(safeText(value), bodyFont));
                valueCell.setBorder(Rectangle.BOX);
                valueCell.setPadding(8f);
                table.addCell(valueCell);
        }

        private void addHeaderCell(PdfPTable table, String text, Font font) {
                PdfPCell cell = new PdfPCell(new Phrase(text, font));
                cell.setBorder(Rectangle.BOX);
                cell.setPadding(6f);
                cell.setBackgroundColor(new java.awt.Color(203, 213, 225));
                table.addCell(cell);
        }

        private void addBodyCell(PdfPTable table, String text, Font font) {
                PdfPCell cell = new PdfPCell(new Phrase(safeText(text), font));
                cell.setBorder(Rectangle.BOX);
                cell.setPadding(6f);
                table.addCell(cell);
        }

        private String safeText(String value) {
                return value == null || value.isBlank() ? "-" : value;
        }

        private String sanitizeFilePart(String value) {
                if (value == null || value.isBlank()) {
                        return "report";
                }

                return value
                                .trim()
                                .replaceAll("[\\\\/:*?\"<>|]", "-")
                                .replaceAll("\\s+", "_");
        }
}