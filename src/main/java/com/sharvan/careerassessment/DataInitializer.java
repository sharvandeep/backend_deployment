package com.sharvan.careerassessment;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sharvan.careerassessment.entity.*;
import com.sharvan.careerassessment.repository.*;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BranchRepository branchRepository;
    private final BranchSkillRepository branchSkillRepository;
    private final UserRepository userRepository;
    private final CareerPathRepository careerPathRepository;
    private final PersonalityTestRepository personalityTestRepository;
    private final PersonalityQuestionRepository personalityQuestionRepository;
    private final BCryptPasswordEncoder passwordEncoder;   // ✅ ADDED

    public DataInitializer(
            BranchRepository branchRepository,
            BranchSkillRepository branchSkillRepository,
            UserRepository userRepository,
            CareerPathRepository careerPathRepository,
            PersonalityTestRepository personalityTestRepository,
            PersonalityQuestionRepository personalityQuestionRepository,
            BCryptPasswordEncoder passwordEncoder   // ✅ ADDED
    ) {
        this.branchRepository = branchRepository;
        this.branchSkillRepository = branchSkillRepository;
        this.userRepository = userRepository;
        this.careerPathRepository = careerPathRepository;
        this.personalityTestRepository = personalityTestRepository;
        this.personalityQuestionRepository = personalityQuestionRepository;
        this.passwordEncoder = passwordEncoder;   // ✅ ADDED
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        System.out.println("\n========================================");
        System.out.println("      DATA INITIALIZER STARTING");
        System.out.println("========================================\n");

        initializeBranches();
        initializeBranchSkills();
        initializeAdmin();        // ✅ Admin now encrypted
        initializeCareerPaths();
        initializePersonalityTest();

        System.out.println("\n========================================");
        System.out.println("      DATA INITIALIZER COMPLETE");
        System.out.println("========================================\n");
    }

    // =====================================================
    // 1️⃣ BRANCHES
    // =====================================================
    private void initializeBranches() {
        String[] branches = {"CSE", "ECE", "CSIT", "AIDS", "MECH", "BIO-TECH"};
        for (String branchName : branches) {
            if (branchRepository.findByName(branchName).isEmpty()) {
                branchRepository.save(new BranchEntity(branchName));
                System.out.println("✅ Created branch: " + branchName);
            }
        }
    }

    // =====================================================
    // 2️⃣ ADMIN (Encrypted Password)
    // =====================================================
    private void initializeAdmin() {

        if (userRepository.findByEmail("admin@career.com").isEmpty()) {

            UserEntity admin = new UserEntity();
            admin.setName("System Admin");
            admin.setEmail("admin@career.com");

            // 🔐 ENCRYPTED PASSWORD
            admin.setPassword(passwordEncoder.encode("admin123"));

            admin.setRole(Role.ADMIN);
            admin.setUserCode("A1");

            userRepository.save(admin);

            System.out.println("✅ Default Admin created (password encrypted)");
        }
    }

    // =====================================================
    // 3️⃣ BRANCH SKILLS (Seed only if empty)
    // =====================================================
    private void initializeBranchSkills() {

        if (branchSkillRepository.count() > 0) {
            System.out.println("✅ Branch skills already exist, skipping...");
            return;
        }

        System.out.println("🌱 Seeding branch skills...");

        BranchEntity cse = branchRepository.findByName("CSE").orElse(null);
        if (cse != null) {
            addSkills(cse, "C", "Java", "Python",
                    "Data Structures", "DBMS",
                    "Web Development", "Operating Systems",
                    "Computer Networks");
        }

        BranchEntity ece = branchRepository.findByName("ECE").orElse(null);
        if (ece != null) {
            addSkills(ece, "Digital Electronics", "Analog Electronics",
                    "Signals & Systems", "Embedded Systems",
                    "VLSI", "Communication Systems", "Microprocessors");
        }

        BranchEntity aids = branchRepository.findByName("AIDS").orElse(null);
        if (aids != null) {
            addSkills(aids, "Python", "Machine Learning",
                    "Statistics", "Data Analysis",
                    "Deep Learning", "Big Data",
                    "Data Visualization");
        }

        BranchEntity mech = branchRepository.findByName("MECH").orElse(null);
        if (mech != null) {
            addSkills(mech, "Thermodynamics", "Mechanics",
                    "CAD/CAM", "Manufacturing",
                    "Fluid Mechanics", "Material Science",
                    "Robotics");
        }

        System.out.println("✅ Branch skills seeded successfully");
    }

    private void addSkills(BranchEntity branch, String... skillNames) {
        for (String skillName : skillNames) {
            BranchSkillEntity skill = new BranchSkillEntity();
            skill.setSkillName(skillName);
            skill.setBranch(branch);
            branchSkillRepository.save(skill);
        }
    }

    // =====================================================
    // 4️⃣ CAREER PATHS (Seed only if empty)
    // =====================================================
    private void initializeCareerPaths() {

        if (careerPathRepository.count() > 0) {
            System.out.println("✅ Career paths already exist, skipping...");
            return;
        }

        System.out.println("🌱 Seeding career paths...");

        BranchEntity cse = branchRepository.findByName("CSE").orElse(null);

        createCareerPath("Backend Developer",
                "Build server-side applications",
                "Java,Python,DBMS",
                60,
                cse,
                "CONSCIENTIOUSNESS,OPENNESS");

        createCareerPath("Full Stack Developer",
                "Build frontend and backend systems",
                "Java,Python,Web Development,DBMS",
                65,
                cse,
                "OPENNESS,CONSCIENTIOUSNESS,EXTRAVERSION");
    }

    private void createCareerPath(String name,
                                  String description,
                                  String skills,
                                  int minPercent,
                                  BranchEntity branch,
                                  String personalityTraits) {

        CareerPathEntity career = new CareerPathEntity();
        career.setCareerName(name);
        career.setDescription(description);
        career.setRequiredSkills(skills);
        career.setMinimumSkillPercentage(minPercent);
        career.setBranch(branch);
        career.setIdealPersonalityTraits(personalityTraits);

        careerPathRepository.save(career);
    }

    // =====================================================
    // 5️⃣ PERSONALITY TEST
    // =====================================================
    private void initializePersonalityTest() {

        if (personalityTestRepository.count() > 0) {
            System.out.println("✅ Personality test already exists, skipping...");
            return;
        }

        System.out.println("🎭 Creating Big Five Personality Test...");

        PersonalityTestEntity test = new PersonalityTestEntity();
        test.setTitle("Big Five Personality Assessment");
        test.setDescription("Big Five (OCEAN) personality model.");
        test.setTotalQuestions(25);
        test.setEstimatedMinutes(10);
        test.setActive(true);
        test.setCreatedAt(LocalDateTime.now());

        PersonalityTestEntity savedTest = personalityTestRepository.save(test);

        int order = 1;

        createQuestion(savedTest, "I enjoy trying new and different activities.",
                PersonalityTrait.OPENNESS, true, order++);

        // (You can keep the rest of your 25 questions here unchanged)
    }

    private void createQuestion(PersonalityTestEntity test,
                                String questionText,
                                PersonalityTrait trait,
                                boolean positiveDirection,
                                int order) {

        PersonalityQuestionEntity question = new PersonalityQuestionEntity();
        question.setPersonalityTest(test);
        question.setQuestionText(questionText);
        question.setTrait(trait);
        question.setPositiveDirection(positiveDirection);
        question.setQuestionOrder(order);

        personalityQuestionRepository.save(question);
    }
}