package com.zhes.homestaybackend.controller;

import com.zhes.homestaybackend.entity.AdminNotification;
import com.zhes.homestaybackend.entity.UserMessage;
import com.zhes.homestaybackend.repository.AdminNotificationRepository;
import com.zhes.homestaybackend.repository.UserMessageRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class MessageController {
    private final UserMessageRepository userMessageRepository;
    private final AdminNotificationRepository adminNotificationRepository;

    public MessageController(UserMessageRepository userMessageRepository,
                             AdminNotificationRepository adminNotificationRepository) {
        this.userMessageRepository = userMessageRepository;
        this.adminNotificationRepository = adminNotificationRepository;
    }

    @GetMapping("/api/messages")
    public List<UserMessage> getUserMessages(@RequestParam Long userId) {
        return userMessageRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @PostMapping("/api/messages")
    public Map<String, Object> createMessage(@RequestBody UserMessage payload) {
        Map<String, Object> result = new HashMap<>();

        if (payload.getUserId() == null || payload.getUsername() == null || payload.getUsername().isBlank()
            || payload.getContent() == null || payload.getContent().isBlank()) {
            result.put("code", 400);
            result.put("message", "留言信息不完整");
            return result;
        }

        UserMessage saved = userMessageRepository.save(payload);
        createNotification("message", "新用户留言", payload.getUsername() + " 提交了新的留言");

        result.put("code", 200);
        result.put("message", "Created");
        result.put("data", saved);
        return result;
    }

    @GetMapping("/api/admin/messages")
    public List<UserMessage> getAdminMessages() {
        return userMessageRepository.findAllByOrderByCreatedAtDesc();
    }

    @PutMapping("/api/admin/messages/{id}/reply")
    public Map<String, Object> replyMessage(@PathVariable Long id, @RequestBody UserMessage payload) {
        Map<String, Object> result = new HashMap<>();
        UserMessage message = userMessageRepository.findById(id).orElse(null);

        if (message == null) {
            result.put("code", 404);
            result.put("message", "留言不存在");
            return result;
        }

        message.setReply(payload.getReply());
        message.setStatus("replied");
        message.setRepliedAt(LocalDateTime.now());
        UserMessage saved = userMessageRepository.save(message);

        result.put("code", 200);
        result.put("message", "Replied");
        result.put("data", saved);
        return result;
    }

    @DeleteMapping("/api/admin/messages/{id}")
    public Map<String, Object> deleteMessage(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        if (!userMessageRepository.existsById(id)) {
            result.put("code", 404);
            result.put("message", "留言不存在");
            return result;
        }

        userMessageRepository.deleteById(id);
        result.put("code", 200);
        result.put("message", "Deleted");
        return result;
    }

    @GetMapping("/api/admin/notifications/unread")
    public List<AdminNotification> getUnreadNotifications() {
        return adminNotificationRepository.findByReadFlagFalseOrderByCreatedAtDesc();
    }

    @PutMapping("/api/admin/notifications/read")
    public Map<String, Object> markNotificationsRead(@RequestBody List<Long> ids) {
        Map<String, Object> result = new HashMap<>();
        List<AdminNotification> notifications = adminNotificationRepository.findAllById(ids);
        notifications.forEach(item -> item.setReadFlag(true));
        adminNotificationRepository.saveAll(notifications);
        result.put("code", 200);
        result.put("message", "Updated");
        return result;
    }

    private void createNotification(String type, String title, String content) {
        AdminNotification notification = new AdminNotification();
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        adminNotificationRepository.save(notification);
    }
}
