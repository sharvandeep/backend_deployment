package com.sharvan.careerassessment.service;

import com.sharvan.careerassessment.dto.*;
import com.sharvan.careerassessment.dto.PersonalityResultDTO.TraitScore;
import com.sharvan.careerassessment.dto.PersonalitySubmissionRequest.PersonalityAnswerRequest;
import com.sharvan.careerassessment.entity.*;
import com.sharvan.careerassessment.repository.*;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonalityService {

    private final PersonalityTestRepository testRepository;
    private final PersonalityQuestionRepository questionRepository;
    private final PersonalityResultRepository resultRepository;
    private final PersonalityResponseRepository responseRepository;
    private final UserRepository userRepository;

    public PersonalityService(
            PersonalityTestRepository testRepository,
            PersonalityQuestionRepository questionRepository,
            PersonalityResultRepository resultRepository,
            PersonalityResponseRepository responseRepository,
            UserRepository userRepository
    ) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
        this.responseRepository = responseRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // GET ALL ACTIVE TESTS (FOR STUDENT)
    // ==========================================

    public List<PersonalityTestDTO> getAvailableTests(Long studentId) {
        List<PersonalityTestEntity> tests = testRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        
        return tests.stream().map(test -> {
            boolean alreadyTaken = resultRepository.existsByStudent_IdAndPersonalityTest_Id(studentId, test.getId());
            return new PersonalityTestDTO(
                test.getId(),
                test.getTitle(),
                test.getDescription(),
                test.getTotalQuestions(),
                test.getEstimatedMinutes(),
                test.isActive(),
                alreadyTaken
            );
        }).collect(Collectors.toList());
    }

    // ==========================================
    // GET TEST QUESTIONS (FOR TAKING TEST)
    // ==========================================

    public List<PersonalityQuestionDTO> getTestQuestions(Long testId) {
        List<PersonalityQuestionEntity> questions = 
            questionRepository.findByPersonalityTest_IdOrderByQuestionOrderAsc(testId);
        
        return questions.stream()
            .map(q -> new PersonalityQuestionDTO(q.getId(), q.getQuestionText(), q.getQuestionOrder()))
            .collect(Collectors.toList());
    }

    // ==========================================
    // SUBMIT PERSONALITY TEST
    // ==========================================

    @Transactional
    public PersonalityResultDTO submitTest(PersonalitySubmissionRequest request) {
        // 1. Validate student
        UserEntity student = userRepository.findById(request.getStudentId())
            .orElseThrow(() -> new RuntimeException("Student not found"));

        // 2. Validate test
        PersonalityTestEntity test = testRepository.findById(request.getTestId())
            .orElseThrow(() -> new RuntimeException("Personality test not found"));

        // 3. Check if already taken
        if (resultRepository.existsByStudent_IdAndPersonalityTest_Id(request.getStudentId(), request.getTestId())) {
            throw new RuntimeException("You have already taken this personality test");
        }

        // 4. Save individual responses
        for (PersonalityAnswerRequest answer : request.getAnswers()) {
            PersonalityQuestionEntity question = questionRepository.findById(answer.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found: " + answer.getQuestionId()));

            PersonalityResponseEntity response = new PersonalityResponseEntity();
            response.setStudent(student);
            response.setPersonalityTest(test);
            response.setQuestion(question);
            response.setResponse(answer.getResponse());
            response.setSubmittedAt(LocalDateTime.now());
            responseRepository.save(response);
        }

        // 5. Calculate trait scores
        Map<PersonalityTrait, Double> traitScores = calculateTraitScores(
            request.getStudentId(), 
            request.getTestId()
        );

        // 6. Determine dominant and secondary traits
        List<Map.Entry<PersonalityTrait, Double>> sortedTraits = traitScores.entrySet()
            .stream()
            .sorted(Map.Entry.<PersonalityTrait, Double>comparingByValue().reversed())
            .collect(Collectors.toList());

        PersonalityTrait dominant = sortedTraits.get(0).getKey();
        PersonalityTrait secondary = sortedTraits.get(1).getKey();

        // 7. Save result
        PersonalityResultEntity result = new PersonalityResultEntity();
        result.setStudent(student);
        result.setPersonalityTest(test);
        result.setOpennessScore(traitScores.get(PersonalityTrait.OPENNESS));
        result.setConscientiousnessScore(traitScores.get(PersonalityTrait.CONSCIENTIOUSNESS));
        result.setExtraversionScore(traitScores.get(PersonalityTrait.EXTRAVERSION));
        result.setAgreeablenessScore(traitScores.get(PersonalityTrait.AGREEABLENESS));
        result.setNeuroticismScore(traitScores.get(PersonalityTrait.NEUROTICISM));
        result.setDominantTrait(dominant);
        result.setSecondaryTrait(secondary);
        result.setSubmittedAt(LocalDateTime.now());
        
        PersonalityResultEntity savedResult = resultRepository.save(result);

        return mapToResultDTO(savedResult);
    }

    // ==========================================
    // CALCULATE TRAIT SCORES
    // ==========================================

    private Map<PersonalityTrait, Double> calculateTraitScores(Long studentId, Long testId) {
        List<PersonalityResponseEntity> responses = 
            responseRepository.findByStudent_IdAndPersonalityTest_Id(studentId, testId);

        Map<PersonalityTrait, List<Integer>> traitResponses = new HashMap<>();
        
        // Initialize map with empty lists
        for (PersonalityTrait trait : PersonalityTrait.values()) {
            traitResponses.put(trait, new ArrayList<>());
        }

        // Group responses by trait
        for (PersonalityResponseEntity response : responses) {
            PersonalityQuestionEntity question = response.getQuestion();
            PersonalityTrait trait = question.getTrait();
            
            int score = response.getResponse();
            
            // Reverse score if negative direction
            if (!question.isPositiveDirection()) {
                score = 6 - score;  // Convert 1->5, 2->4, 3->3, 4->2, 5->1
            }
            
            traitResponses.get(trait).add(score);
        }

        // Calculate percentage for each trait
        Map<PersonalityTrait, Double> traitScores = new HashMap<>();
        
        for (PersonalityTrait trait : PersonalityTrait.values()) {
            List<Integer> scores = traitResponses.get(trait);
            
            if (scores.isEmpty()) {
                traitScores.put(trait, 50.0);  // Default to neutral
            } else {
                double average = scores.stream().mapToInt(Integer::intValue).average().orElse(3.0);
                // Convert 1-5 scale to 0-100 percentage
                // (average - 1) / 4 * 100
                double percentage = ((average - 1) / 4.0) * 100;
                traitScores.put(trait, Math.round(percentage * 10.0) / 10.0);  // Round to 1 decimal
            }
        }

        return traitScores;
    }

    // ==========================================
    // GET STUDENT'S PERSONALITY RESULTS
    // ==========================================

    public List<PersonalityResultDTO> getStudentResults(Long studentId) {
        List<PersonalityResultEntity> results = resultRepository.findByStudent_Id(studentId);
        return results.stream()
            .map(this::mapToResultDTO)
            .collect(Collectors.toList());
    }

    // ==========================================
    // GET LATEST PERSONALITY RESULT
    // ==========================================

    public PersonalityResultDTO getLatestResult(Long studentId) {
        return resultRepository.findTopByStudent_IdOrderBySubmittedAtDesc(studentId)
            .map(this::mapToResultDTO)
            .orElse(null);
    }

    // ==========================================
    // GET SPECIFIC RESULT
    // ==========================================

    public PersonalityResultDTO getResult(Long resultId) {
        return resultRepository.findById(resultId)
            .map(this::mapToResultDTO)
            .orElseThrow(() -> new RuntimeException("Result not found"));
    }

    // ==========================================
    // EXPORT RESULT PDF
    // ==========================================

    public PersonalityPdfReport exportResultPdf(Long resultId) {
        PersonalityResultEntity result = resultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Result not found"));

        PersonalityResultDTO dto = mapToResultDTO(result);
        byte[] pdfBytes = generatePersonalityPdf(dto, result);

        String fileName = sanitizeFilePart(result.getPersonalityTest().getTitle()) + "_"
                + sanitizeFilePart(result.getStudent().getName()) + "_personality_report.pdf";

        return new PersonalityPdfReport(fileName, pdfBytes);
    }

    // ==========================================
    // ADMIN: GET ALL TESTS
    // ==========================================

    public List<PersonalityTestDTO> getAllTests() {
        return testRepository.findAll().stream()
            .map(test -> new PersonalityTestDTO(
                test.getId(),
                test.getTitle(),
                test.getDescription(),
                test.getTotalQuestions(),
                test.getEstimatedMinutes(),
                test.isActive(),
                false
            ))
            .collect(Collectors.toList());
    }

    // ==========================================
    // HELPER: MAP TO DTO
    // ==========================================

    private PersonalityResultDTO mapToResultDTO(PersonalityResultEntity result) {
        PersonalityResultDTO dto = new PersonalityResultDTO();
        dto.setId(result.getId());
        dto.setTestId(result.getPersonalityTest().getId());
        dto.setTestTitle(result.getPersonalityTest().getTitle());
        dto.setSubmittedAt(result.getSubmittedAt());
        dto.setOpennessScore(result.getOpennessScore());
        dto.setConscientiousnessScore(result.getConscientiousnessScore());
        dto.setExtraversionScore(result.getExtraversionScore());
        dto.setAgreeablenessScore(result.getAgreeablenessScore());
        dto.setNeuroticismScore(result.getNeuroticismScore());

        // Set dominant trait info
        PersonalityTrait dominant = result.getDominantTrait();
        dto.setDominantTrait(dominant.getDisplayName());
        dto.setDominantTraitDescription(dominant.getHighDescription());

        PersonalityTrait secondary = result.getSecondaryTrait();
        dto.setSecondaryTrait(secondary.getDisplayName());
        dto.setSecondaryTraitDescription(secondary.getHighDescription());

        // Build trait scores list for chart
        List<TraitScore> traitScores = new ArrayList<>();
        for (PersonalityTrait trait : PersonalityTrait.values()) {
            double score = result.getScoreByTrait(trait);
            String level = score >= 70 ? "HIGH" : (score >= 40 ? "MODERATE" : "LOW");
            String description = score >= 50 ? trait.getHighDescription() : trait.getLowDescription();
            
            traitScores.add(new TraitScore(
                trait.name(),
                trait.getDisplayName(),
                score,
                level,
                description
            ));
        }
        dto.setTraitScores(traitScores);

        return dto;
    }

    private byte[] generatePersonalityPdf(PersonalityResultDTO dto, PersonalityResultEntity rawResult) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 28, 28, 28, 28);
            PdfWriter.getInstance(document, output);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font sectionTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
            Font body = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font bodyBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

            Paragraph title = new Paragraph("Personality Assessment Report", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            Paragraph generated = new Paragraph(
                    "Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")),
                    subTitleFont
            );
            generated.setAlignment(Paragraph.ALIGN_CENTER);
            generated.setSpacingAfter(12f);
            document.add(generated);

            PdfPTable summary = new PdfPTable(new float[]{1.4f, 2.6f});
            summary.setWidthPercentage(100);
            summary.setSpacingAfter(14f);
            addSummaryRow(summary, "Student", rawResult.getStudent().getName(), body);
            addSummaryRow(summary, "Test", rawResult.getPersonalityTest().getTitle(), body);
            addSummaryRow(summary, "Completed", String.valueOf(rawResult.getSubmittedAt()), body);
            addSummaryRow(summary, "Primary Trait", dto.getDominantTrait(), bodyBold);
            addSummaryRow(summary, "Secondary Trait", dto.getSecondaryTrait(), bodyBold);
            document.add(summary);

            Paragraph insightsTitle = new Paragraph("Profile Insights", sectionTitle);
            insightsTitle.setSpacingAfter(6f);
            document.add(insightsTitle);

            Paragraph insights = new Paragraph(
                    "Your profile indicates a strong inclination towards " + dto.getDominantTrait()
                            + " with supporting characteristics from " + dto.getSecondaryTrait() + ". "
                            + "This combination can influence learning style, teamwork behavior, and ideal career environments.",
                    body
            );
            insights.setSpacingAfter(12f);
            document.add(insights);

            Paragraph traitTitle = new Paragraph("Big Five Trait Breakdown", sectionTitle);
            traitTitle.setSpacingAfter(6f);
            document.add(traitTitle);

            PdfPTable traitTable = new PdfPTable(new float[]{1.6f, 0.7f, 0.9f, 2.8f});
            traitTable.setWidthPercentage(100);

            addHeaderCell(traitTable, "Trait", bodyBold);
            addHeaderCell(traitTable, "Score", bodyBold);
            addHeaderCell(traitTable, "Level", bodyBold);
            addHeaderCell(traitTable, "Interpretation", bodyBold);

            for (TraitScore ts : dto.getTraitScores()) {
                addBodyCell(traitTable, ts.getDisplayName(), body);
                addBodyCell(traitTable, String.format(Locale.US, "%.1f%%", ts.getScore()), body);
                addBodyCell(traitTable, ts.getLevel(), bodyBold);
                addBodyCell(traitTable, ts.getDescription(), body);
            }

            document.add(traitTable);

            Paragraph explainTitle = new Paragraph("What This Means", sectionTitle);
            explainTitle.setSpacingBefore(12f);
            explainTitle.setSpacingAfter(6f);
            document.add(explainTitle);

            Paragraph explainBody = new Paragraph(
                    "High scores indicate stronger expression of a trait. Moderate scores suggest flexibility, and low scores indicate "
                            + "a preference for the opposite behavior style. Use this report as a guide for self-awareness and career planning, "
                            + "not as a strict label.",
                    body
            );
            document.add(explainBody);

            document.close();
            return output.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate personality PDF report", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while generating personality PDF report", e);
        }
    }

    private void addSummaryRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell left = new PdfPCell(new Phrase(label, font));
        left.setPadding(8f);
        left.setBackgroundColor(new java.awt.Color(232, 240, 254));
        left.setBorder(Rectangle.BOX);
        table.addCell(left);

        PdfPCell right = new PdfPCell(new Phrase(value == null ? "-" : value, font));
        right.setPadding(8f);
        right.setBorder(Rectangle.BOX);
        table.addCell(right);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6f);
        cell.setBackgroundColor(new java.awt.Color(203, 213, 225));
        cell.setBorder(Rectangle.BOX);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null || text.isBlank() ? "-" : text, font));
        cell.setPadding(6f);
        cell.setBorder(Rectangle.BOX);
        table.addCell(cell);
    }

    private String sanitizeFilePart(String value) {
        if (value == null || value.isBlank()) {
            return "report";
        }

        return value.trim()
                .replaceAll("[\\\\/:*?\"<>|]", "-")
                .replaceAll("\\s+", "_");
    }

    public record PersonalityPdfReport(String fileName, byte[] bytes) {}
}
