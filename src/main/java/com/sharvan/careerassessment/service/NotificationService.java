package com.sharvan.careerassessment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sharvan.careerassessment.entity.NotificationEntity;
import com.sharvan.careerassessment.entity.UserEntity;
import com.sharvan.careerassessment.repository.NotificationRepository;
import com.sharvan.careerassessment.repository.UserRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // =====================================================
    // 1️⃣ Create Notification
    // =====================================================
    @Transactional
    public NotificationEntity createNotification(Long recipientId,
                                                  Long triggeredById,
                                                  String title,
                                                  String message,
                                                  String notificationType,
                                                  String actionUrl,
                                                  Long relatedEntityId) {

        // Validate users exist
        UserEntity recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        UserEntity triggeredBy = userRepository.findById(triggeredById)
                .orElseThrow(() -> new RuntimeException("Triggered by user not found"));

        // Create notification
        NotificationEntity notification = new NotificationEntity();
        notification.setRecipient(recipient);
        notification.setTriggeredBy(triggeredBy);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setActionUrl(actionUrl);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    // =====================================================
    // 2️⃣ Get all notifications for a user
    // =====================================================
    public List<NotificationEntity> getNotificationsForUser(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    // =====================================================
    // 3️⃣ Get unread notifications for a user
    // =====================================================
    public List<NotificationEntity> getUnreadNotificationsForUser(Long userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    // =====================================================
    // 4️⃣ Count unread notifications for a user
    // =====================================================
    public long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    // =====================================================
    // 5️⃣ Mark notification as read
    // =====================================================
    @Transactional
    public NotificationEntity markAsRead(Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        }

        return notificationRepository.save(notification);
    }

    // =====================================================
    // 6️⃣ Mark all notifications as read for a user
    // =====================================================
    @Transactional
    public void markAllAsRead(Long userId) {
        List<NotificationEntity> unreadNotifications = getUnreadNotificationsForUser(userId);
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unreadNotifications);
    }

    // =====================================================
    // 7️⃣ Delete notification
    // =====================================================
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // =====================================================
    // 8️⃣ Delete all notifications for a user
    // =====================================================
    @Transactional
    public void deleteAllNotificationsForUser(Long userId) {
        List<NotificationEntity> notifications = getNotificationsForUser(userId);
        notificationRepository.deleteAll(notifications);
    }

    // =====================================================
    // 9️⃣ Get notifications by type
    // =====================================================
    public List<NotificationEntity> getNotificationsByType(Long userId, String notificationType) {
        return notificationRepository.findByRecipientIdAndNotificationTypeOrderByCreatedAtDesc(userId, notificationType);
    }

    // =====================================================
    // 🔟 Get recent notifications (limit 10)
    // =====================================================
    public List<NotificationEntity> getRecentNotifications(Long userId) {
        return notificationRepository.findRecentNotifications(userId);
    }

    // =====================================================
    // 1️⃣1️⃣ Get notification history (read + unread)
    // =====================================================
    public List<NotificationEntity> getNotificationHistory(Long userId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        Pageable pageable = PageRequest.of(0, safeLimit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findByRecipientId(userId, pageable).getContent();
    }

    // =====================================================
    // 1️⃣2️⃣ One-time migration for legacy review URLs
    // =====================================================
    @Transactional
    public int migrateLegacyReviewActionUrls() {
        return notificationRepository.migrateLegacyReviewActionUrls();
    }
}
