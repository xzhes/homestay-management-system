package com.zhes.homestaybackend.controller;

import com.zhes.homestaybackend.entity.Homestay;
import com.zhes.homestaybackend.repository.HomestayRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// 后台房源管理接口（含图片上传）
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/homestays")
public class AdminHomestayController {

    private final HomestayRepository homestayRepository;

    public AdminHomestayController(HomestayRepository homestayRepository) {
        this.homestayRepository = homestayRepository;
    }

    // 上传房源图片，返回可访问路径：/uploads/xxx.jpg
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadImage(@RequestPart("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        if (file == null || file.isEmpty()) {
            result.put("code", 400);
            result.put("message", "File is empty");
            return result;
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            result.put("code", 400);
            result.put("message", "Only image files are allowed");
            return result;
        }

        try {
            Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");
            Files.createDirectories(uploadDir);

            String originalName = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
            String ext = "";
            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex >= 0) {
                ext = originalName.substring(dotIndex);
            }

            String fileName = UUID.randomUUID() + ext;
            Path targetPath = uploadDir.resolve(fileName);
            file.transferTo(targetPath.toFile());

            result.put("code", 200);
            result.put("message", "Uploaded");
            result.put("data", "/uploads/" + fileName);
            return result;
        } catch (IOException e) {
            result.put("code", 500);
            result.put("message", "Upload failed");
            return result;
        }
    }

    @GetMapping
    public List<Homestay> getHomestays() {
        return homestayRepository.findAll();
    }

    @PostMapping
    public Map<String, Object> createHomestay(@RequestBody Homestay homestay) {
        Map<String, Object> result = new HashMap<>();

        if (homestay.getName() == null || homestay.getName().isBlank() || homestay.getPrice() == null) {
            result.put("code", 400);
            result.put("message", "Name and price are required");
            return result;
        }

        Homestay saved = homestayRepository.save(homestay);
        result.put("code", 200);
        result.put("message", "Created");
        result.put("data", saved);
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateHomestay(@PathVariable Long id, @RequestBody Homestay payload) {
        Map<String, Object> result = new HashMap<>();
        Homestay homestay = homestayRepository.findById(id).orElse(null);

        if (homestay == null) {
            result.put("code", 404);
            result.put("message", "Homestay not found");
            return result;
        }

        if (payload.getName() != null && !payload.getName().isBlank()) {
            homestay.setName(payload.getName());
        }
        if (payload.getPrice() != null) {
            homestay.setPrice(payload.getPrice());
        }
        if (payload.getDescription() != null) {
            homestay.setDescription(payload.getDescription());
        }
        if (payload.getImageUrl() != null) {
            homestay.setImageUrl(payload.getImageUrl());
        }

        Homestay saved = homestayRepository.save(homestay);
        result.put("code", 200);
        result.put("message", "Updated");
        result.put("data", saved);
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteHomestay(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        if (!homestayRepository.existsById(id)) {
            result.put("code", 404);
            result.put("message", "Homestay not found");
            return result;
        }

        homestayRepository.deleteById(id);
        result.put("code", 200);
        result.put("message", "Deleted");
        return result;
    }
}
