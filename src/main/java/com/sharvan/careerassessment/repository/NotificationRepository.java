package com.sharvan.careerassessment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sharvan.careerassessment.entity.*;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByRecipientId(Long recipientId, Pageable pageable);

    // Get all notifications for a user, ordered by creation date (newest first)
    List<NotificationEntity> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    // Get unread notifications for a user
    List<NotificationEntity> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);

    // Count unread notifications for a user
    long countByRecipientIdAndIsReadFalse(Long recipientId);

    // Get notifications by type
    List<NotificationEntity> findByRecipientIdAndNotificationTypeOrderByCreatedAtDesc(Long recipientId, String notificationType);

    // Get notifications triggered by a specific user
    List<NotificationEntity> findByTriggeredByIdOrderByCreatedAtDesc(Long triggeredById);

    // Custom query to get recent notifications
    @Query("SELECT n FROM NotificationEntity n WHERE n.recipient.id = :recipientId ORDER BY n.createdAt DESC LIMIT 10")
    List<NotificationEntity> findRecentNotifications(@Param("recipientId") Long recipientId);

    @Modifying
    @Query(value = "UPDATE notifications n " +
            "SET n.action_url = CONCAT('/student/review/', SUBSTRING_INDEX(n.action_url, '=', -1), '/', n.recipient_id) " +
            "WHERE n.action_url LIKE '/student/reviews?assessmentId=%'", nativeQuery = true)
    int migrateLegacyReviewActionUrls();
}
