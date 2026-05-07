package com.zhes.homestaybackend.controller;

import com.zhes.homestaybackend.entity.Announcement;
import com.zhes.homestaybackend.repository.AnnouncementRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class AnnouncementController {
    private final AnnouncementRepository announcementRepository;

    public AnnouncementController(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @GetMapping("/api/announcements")
    public List<Announcement> getPublishedAnnouncements() {
        return announcementRepository.findByStatusOrderByCreatedAtDesc("published");
    }

    @GetMapping("/api/admin/announcements")
    public List<Announcement> getAdminAnnouncements() {
        return announcementRepository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping("/api/admin/announcements")
    public Map<String, Object> createAnnouncement(@RequestBody Announcement payload) {
        Map<String, Object> result = new HashMap<>();

        if (payload.getTitle() == null || payload.getTitle().isBlank()
            || payload.getContent() == null || payload.getContent().isBlank()) {
            result.put("code", 400);
            result.put("message", "标题和内容不能为空");
            return result;
        }

        if (payload.getStatus() == null || payload.getStatus().isBlank()) {
            payload.setStatus("published");
        }

        Announcement saved = announcementRepository.save(payload);
        result.put("code", 200);
        result.put("message", "Created");
        result.put("data", saved);
        return result;
    }

    @PutMapping("/api/admin/announcements/{id}")
    public Map<String, Object> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement payload) {
        Map<String, Object> result = new HashMap<>();
        Announcement announcement = announcementRepository.findById(id).orElse(null);

        if (announcement == null) {
            result.put("code", 404);
            result.put("message", "公告不存在");
            return result;
        }

        if (payload.getTitle() != null && !payload.getTitle().isBlank()) {
            announcement.setTitle(payload.getTitle());
        }
        if (payload.getContent() != null && !payload.getContent().isBlank()) {
            announcement.setContent(payload.getContent());
        }
        if (payload.getStatus() != null && !payload.getStatus().isBlank()) {
            announcement.setStatus(payload.getStatus());
        }

        Announcement saved = announcementRepository.save(announcement);
        result.put("code", 200);
        result.put("message", "Updated");
        result.put("data", saved);
        return result;
    }

    @DeleteMapping("/api/admin/announcements/{id}")
    public Map<String, Object> deleteAnnouncement(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        if (!announcementRepository.existsById(id)) {
            result.put("code", 404);
            result.put("message", "公告不存在");
            return result;
        }

        announcementRepository.deleteById(id);
        result.put("code", 200);
        result.put("message", "Deleted");
        return result;
    }
}
