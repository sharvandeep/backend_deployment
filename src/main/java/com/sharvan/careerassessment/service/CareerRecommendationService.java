package com.sharvan.careerassessment.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sharvan.careerassessment.dto.CareerRecommendationDTO;
import com.sharvan.careerassessment.entity.*;
import com.sharvan.careerassessment.repository.*;

@Service
public class CareerRecommendationService {

    private final UserRepository userRepository;
    private final StudentResponseRepository responseRepository;
    private final CareerPathRepository careerPathRepository;
    private final PersonalityResultRepository personalityResultRepository;

    public CareerRecommendationService(
            UserRepository userRepository,
            StudentResponseRepository responseRepository,
            CareerPathRepository careerPathRepository,
            PersonalityResultRepository personalityResultRepository
    ) {
        this.userRepository = userRepository;
        this.responseRepository = responseRepository;
        this.careerPathRepository = careerPathRepository;
        this.personalityResultRepository = personalityResultRepository;
    }

    // =====================================================
    // MAIN METHOD: GET CAREER RECOMMENDATIONS
    // =====================================================
    public List<CareerRecommendationDTO> getRecommendations(Long studentId) {

        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // 1️⃣ Get skill analysis
        Map<String, Double> skillPercentages = calculateSkillPercentages(studentId);

        // 2️⃣ Get latest personality result (if exists)
        Optional<PersonalityResultEntity> personalityResult =
                personalityResultRepository.findTopByStudent_IdOrderBySubmittedAtDesc(studentId);

        // 3️⃣ Fetch career paths (branch specific + general)
        List<CareerPathEntity> careerPaths = new ArrayList<>();

        if (student.getBranch() != null) {
            careerPaths.addAll(
                    careerPathRepository.findByBranch_Id(student.getBranch().getId())
            );
        }

        careerPaths.addAll(
                careerPathRepository.findAll()
                        .stream()
                        .filter(cp -> cp.getBranch() == null)
                        .collect(Collectors.toList())
        );

        // 4️⃣ Calculate recommendation score
        List<CareerRecommendationDTO> recommendations = new ArrayList<>();

        for (CareerPathEntity career : careerPaths) {

            int skillMatch = calculateSkillMatchPercentage(
                    career.getRequiredSkills(),
                    skillPercentages,
                    career.getMinimumSkillPercentage()
            );

            int personalityMatch = 0;
            if (personalityResult.isPresent() && career.getIdealPersonalityTraits() != null) {
                personalityMatch = calculatePersonalityMatchPercentage(
                        career.getIdealPersonalityTraits(),
                        personalityResult.get()
                );
            }

            int combinedMatch;
            if (personalityResult.isPresent() && career.getIdealPersonalityTraits() != null) {
                combinedMatch = (int) (skillMatch * 0.7 + personalityMatch * 0.3);
            } else {
                combinedMatch = skillMatch;
            }

            CareerRecommendationDTO dto = new CareerRecommendationDTO();
            dto.setId(career.getId());
            dto.setCareerName(career.getCareerName());
            dto.setDescription(career.getDescription());
            dto.setRequiredSkills(career.getRequiredSkills());
            dto.setSkillMatchPercentage(skillMatch);
            dto.setPersonalityMatchPercentage(personalityMatch);
            dto.setMatchPercentage(combinedMatch);

            // Match level classification
            if (combinedMatch >= 75) {
                dto.setMatchLevel("STRONG");
            } else if (combinedMatch >= 45) {
                dto.setMatchLevel("MODERATE");
            } else {
                dto.setMatchLevel("WEAK");
            }

            // Explanation logic
            dto.setExplanation(generateExplanation(combinedMatch));

            recommendations.add(dto);
        }

        // 5️⃣ Sort highest first
        recommendations.sort((a, b) -> b.getMatchPercentage() - a.getMatchPercentage());

        // 6️⃣ Return top 5 only
        return recommendations.stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    // =====================================================
    // SKILL PERCENTAGE CALCULATION
    // =====================================================
    private Map<String, Double> calculateSkillPercentages(Long studentId) {

        List<StudentResponseEntity> responses =
                responseRepository.findByStudent_Id(studentId);

        Map<String, Integer> total = new HashMap<>();
        Map<String, Integer> correct = new HashMap<>();

        for (StudentResponseEntity response : responses) {

            String skill = response.getQuestion().getSkillCategory();
            if (skill == null || skill.isEmpty()) continue;

            total.put(skill, total.getOrDefault(skill, 0) + 1);

            if (response.getQuestion().getCorrectAnswer()
                    .equalsIgnoreCase(response.getSelectedAnswer())) {
                correct.put(skill, correct.getOrDefault(skill, 0) + 1);
            }
        }

        Map<String, Double> percentages = new HashMap<>();

        for (String skill : total.keySet()) {
            double percent = (double) correct.getOrDefault(skill, 0)
                    / total.get(skill) * 100;
            percentages.put(skill, percent);
        }

        return percentages;
    }

    // =====================================================
    // IMPROVED SKILL MATCH LOGIC
    // =====================================================
    private int calculateSkillMatchPercentage(
            String requiredSkills,
            Map<String, Double> studentSkills,
            int minimumPercentage
    ) {

        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return 0;
        }

        String[] skills = requiredSkills.split(",");
        double totalScore = 0;

        Map<String, Double> normalizedStudentSkills = new HashMap<>();
        for (Map.Entry<String, Double> entry : studentSkills.entrySet()) {
            normalizedStudentSkills.put(entry.getKey().toUpperCase(), entry.getValue());
        }

        for (String skill : skills) {
            skill = skill.trim().toUpperCase();

            if (normalizedStudentSkills.containsKey(skill)) {
                double studentScore = normalizedStudentSkills.get(skill);

                if (studentScore < minimumPercentage) {
                    totalScore += (studentScore / minimumPercentage) * 50;
                } else {
                    totalScore += studentScore;
                }
            }
        }

        return (int) (totalScore / skills.length);
    }

    // =====================================================
    // PERSONALITY MATCH LOGIC
    // =====================================================
    private int calculatePersonalityMatchPercentage(
            String idealTraits,
            PersonalityResultEntity personalityResult
    ) {

        if (idealTraits == null || idealTraits.isEmpty()) {
            return 0;
        }

        String[] traits = idealTraits.split(",");
        double totalScore = 0;

        for (String traitStr : traits) {
            traitStr = traitStr.trim().toUpperCase();

            try {
                PersonalityTrait trait = PersonalityTrait.valueOf(traitStr);
                double score = personalityResult.getScoreByTrait(trait);
                totalScore += score;
            } catch (IllegalArgumentException e) {
                // Ignore invalid traits
            }
        }

        return (int) (totalScore / traits.length);
    }

    // =====================================================
    // EXPLANATION GENERATOR
    // =====================================================
    private String generateExplanation(int combinedMatch) {

        if (combinedMatch >= 75) {
            return "You show strong alignment in both required skills and personality traits.";
        } else if (combinedMatch >= 45) {
            return "You meet several career requirements but may need improvement in key skills.";
        } else {
            return "Consider strengthening core technical skills to better match this career.";
        }
    }
}