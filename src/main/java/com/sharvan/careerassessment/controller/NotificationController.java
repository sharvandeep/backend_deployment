package com.sharvan.careerassessment.controller;

import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sharvan.careerassessment.entity.NotificationEntity;
import com.sharvan.careerassessment.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // =====================================================
    // ✅ GET ALL NOTIFICATIONS FOR A USER
    // =====================================================
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationEntity>> getNotificationsForUser(
            @PathVariable Long userId) {
        List<NotificationEntity> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    // =====================================================
    // ✅ GET UNREAD NOTIFICATIONS FOR A USER
    // =====================================================
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationEntity>> getUnreadNotifications(
            @PathVariable Long userId) {
        List<NotificationEntity> notifications = notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    // =====================================================
    // ✅ GET UNREAD NOTIFICATION COUNT
    // =====================================================
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadNotificationCount(
            @PathVariable Long userId) {
        long count = notificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(count);
    }

    // =====================================================
    // ✅ GET RECENT NOTIFICATIONS (LIMIT 10)
    // =====================================================
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<NotificationEntity>> getRecentNotifications(
            @PathVariable Long userId) {
        List<NotificationEntity> notifications = notificationService.getRecentNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // =====================================================
    // ✅ GET NOTIFICATION HISTORY (READ + UNREAD)
    // =====================================================
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<NotificationEntity>> getNotificationHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "50") int limit) {
        List<NotificationEntity> notifications = notificationService.getNotificationHistory(userId, limit);
        return ResponseEntity.ok(notifications);
    }

    // =====================================================
    // ✅ EXPORT NOTIFICATION HISTORY AS CSV
    // =====================================================
    @GetMapping("/user/{userId}/history/export")
    public ResponseEntity<byte[]> exportNotificationHistory(
            @PathVariable Long userId) {
        List<NotificationEntity> notifications = notificationService.getNotificationsForUser(userId);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Title,Message,Type,Read,Created At,Read At,Action URL,Related Entity ID,Triggered By\n");

        for (NotificationEntity n : notifications) {
            csv.append(n.getId()).append(",")
                    .append(escapeCsv(n.getTitle())).append(",")
                    .append(escapeCsv(n.getMessage())).append(",")
                    .append(escapeCsv(n.getNotificationType())).append(",")
                    .append(n.isRead()).append(",")
                    .append(escapeCsv(String.valueOf(n.getCreatedAt()))).append(",")
                    .append(escapeCsv(n.getReadAt() != null ? String.valueOf(n.getReadAt()) : "")).append(",")
                    .append(escapeCsv(n.getActionUrl())).append(",")
                    .append(n.getRelatedEntityId() != null ? n.getRelatedEntityId() : "").append(",")
                    .append(escapeCsv(n.getTriggeredBy() != null ? n.getTriggeredBy().getName() : ""))
                    .append("\n");
        }

        byte[] fileBytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        String fileName = "notification-history-user-" + userId + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(fileBytes);
    }

    // =====================================================
    // ✅ GET NOTIFICATIONS BY TYPE
    // =====================================================
    @GetMapping("/user/{userId}/type/{notificationType}")
    public ResponseEntity<List<NotificationEntity>> getNotificationsByType(
            @PathVariable Long userId,
            @PathVariable String notificationType) {
        List<NotificationEntity> notifications = notificationService.getNotificationsByType(userId, notificationType);
        return ResponseEntity.ok(notifications);
    }

    // =====================================================
    // ✅ MARK NOTIFICATION AS READ
    // =====================================================
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationEntity> markAsRead(
            @PathVariable Long notificationId) {
        NotificationEntity notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(notification);
    }

    // =====================================================
    // ✅ MARK ALL NOTIFICATIONS AS READ FOR A USER
    // =====================================================
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<String> markAllAsRead(
            @PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("All notifications marked as read");
    }

    // =====================================================
    // ✅ DELETE NOTIFICATION
    // =====================================================
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(
            @PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("Notification deleted successfully");
    }

    // =====================================================
    // ✅ DELETE ALL NOTIFICATIONS FOR A USER
    // =====================================================
    @DeleteMapping("/user/{userId}/delete-all")
    public ResponseEntity<String> deleteAllNotifications(
            @PathVariable Long userId) {
        notificationService.deleteAllNotificationsForUser(userId);
        return ResponseEntity.ok("All notifications deleted successfully");
    }

    // =====================================================
    // ✅ ONE-TIME MIGRATION FOR LEGACY REVIEW LINKS
    // =====================================================
    @PutMapping("/migrate/legacy-review-links")
    public ResponseEntity<Map<String, Object>> migrateLegacyReviewLinks() {
        int updatedRows = notificationService.migrateLegacyReviewActionUrls();
        return ResponseEntity.ok(Map.of(
                "message", "Legacy review links migration completed",
                "updatedRows", updatedRows
        ));
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "\"\"";
        }

        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
