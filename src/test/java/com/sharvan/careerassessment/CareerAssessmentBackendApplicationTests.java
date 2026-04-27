package com.sharvan.careerassessment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.sharvan.careerassessment.dto.AssessmentRequest;
import com.sharvan.careerassessment.dto.QuestionRequest;
import com.sharvan.careerassessment.entity.AssessmentEntity;
import com.sharvan.careerassessment.entity.BranchEntity;
import com.sharvan.careerassessment.entity.NotificationEntity;
import com.sharvan.careerassessment.entity.Role;
import com.sharvan.careerassessment.entity.UserEntity;
import com.sharvan.careerassessment.repository.BranchRepository;
import com.sharvan.careerassessment.repository.NotificationRepository;
import com.sharvan.careerassessment.repository.UserRepository;
import com.sharvan.careerassessment.service.AssessmentService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CareerAssessmentBackendApplicationTests {

	@Autowired
	private AssessmentService assessmentService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BranchRepository branchRepository;

	@Autowired
	private NotificationRepository notificationRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void testAssessmentCreationSendsNotificationsToStudents() {
		// =====================================================
		// Step 1: Create a branch
		// =====================================================
		BranchEntity branch = new BranchEntity();
		branch.setName("Test-Branch-" + System.currentTimeMillis());
		BranchEntity savedBranch = branchRepository.save(branch);

		// =====================================================
		// Step 2: Create a faculty
		// =====================================================
		UserEntity faculty = new UserEntity();
		faculty.setName("Dr. Test Faculty");
		faculty.setEmail("faculty" + System.currentTimeMillis() + "@test.com");
		faculty.setPassword("password");
		faculty.setRole(Role.FACULTY);
		faculty.setBranch(savedBranch);
		UserEntity savedFaculty = userRepository.save(faculty);

		// =====================================================
		// Step 3: Create test students
		// =====================================================
		List<UserEntity> students = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			UserEntity student = new UserEntity();
			student.setName("Test Student " + i);
			student.setEmail("student" + i + System.currentTimeMillis() + "@test.com");
			student.setPassword("password");
			student.setRole(Role.STUDENT);
			student.setBranch(savedBranch);
			students.add(userRepository.save(student));
		}

		// =====================================================
		// Step 4: Create an assessment
		// =====================================================
		AssessmentRequest assessmentRequest = new AssessmentRequest();
		assessmentRequest.setTitle("Test Assessment - Notifications");
		assessmentRequest.setDescription("Testing notification creation");

		// Add a test question
		List<QuestionRequest> questions = new ArrayList<>();
		QuestionRequest question = new QuestionRequest();
		question.setQuestionText("What is the time complexity of quick sort?");
		question.setOptionA("O(n)");
		question.setOptionB("O(n log n)");
		question.setOptionC("O(n²)");
		question.setOptionD("O(log n)");
		question.setCorrectAnswer("B");
		question.setSkillCategory("Data Structures");
		questions.add(question);
		assessmentRequest.setQuestions(questions);

		// Create the assessment (this should trigger notifications)
		AssessmentEntity createdAssessment = assessmentService.createAssessment(
				savedFaculty.getId(), 
				assessmentRequest
		);

		// =====================================================
		// Step 5: Verify notifications were created
		// =====================================================
		assertNotNull(createdAssessment, "Assessment should be created");
		assertNotNull(createdAssessment.getId(), "Assessment should have an ID");

		// Check that notifications were created for each student
		for (UserEntity student : students) {
			List<NotificationEntity> notifications = 
				notificationRepository.findByRecipientIdOrderByCreatedAtDesc(student.getId());

			assertFalse(notifications.isEmpty(), 
				"Student " + student.getId() + " should have at least one notification");

			// Find the ASSESSMENT_AVAILABLE notification
			NotificationEntity assessmentNotification = notifications.stream()
				.filter(n -> "ASSESSMENT_AVAILABLE".equals(n.getNotificationType()))
				.filter(n -> createdAssessment.getId().equals(n.getRelatedEntityId()))
				.findFirst()
				.orElse(null);

			assertNotNull(assessmentNotification, 
				"Student " + student.getId() + " should have ASSESSMENT_AVAILABLE notification");

			// Verify notification details
			assertEquals("New Assessment Available", assessmentNotification.getTitle());
			assertTrue(assessmentNotification.getMessage()
				.contains(createdAssessment.getTitle()), 
				"Notification message should contain assessment title");
			assertTrue(assessmentNotification.getMessage()
				.contains(savedFaculty.getName()),
				"Notification message should contain faculty name");
			assertEquals("/student/assessments", assessmentNotification.getActionUrl(),
				"Notification action URL should be /student/assessments");
			assertFalse(assessmentNotification.isRead(), 
				"Notification should initially be unread");
		}

		System.out.println("✅ Test PASSED: Assessment created and notifications sent to " + students.size() + " students");
	}

}
