package com.zhes.homestaybackend.controller;

import com.zhes.homestaybackend.entity.Homestay;
import com.zhes.homestaybackend.repository.HomestayRepository;
import com.zhes.homestaybackend.repository.ReservationRepository;
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

// 后台房源管理接口
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/homestays")
public class AdminHomestayController {

    private final HomestayRepository homestayRepository;
    private final ReservationRepository reservationRepository;

    // 只有这些状态存在时，禁止编辑/删除房源
    private static final List<String> ACTIVE_RESERVATION_STATUSES = List.of(
        "待确认", "待入住", "已入住", "已预订", "BOOKED", "CHECKED_IN"
    );

    public AdminHomestayController(HomestayRepository homestayRepository,
                                   ReservationRepository reservationRepository) {
        this.homestayRepository = homestayRepository;
        this.reservationRepository = reservationRepository;
    }

    // 上传房源图片
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadImage(@RequestPart("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        if (file == null || file.isEmpty()) {
            result.put("code", 400);
            result.put("message", "文件为空");
            return result;
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            result.put("code", 400);
            result.put("message", "仅支持图片文件");
            return result;
        }

        try {
            // 上传到后端项目根目录下的 uploads/ 目录
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
            result.put("message", "上传成功");
            result.put("data", "/uploads/" + fileName);
            return result;
        } catch (IOException e) {
            result.put("code", 500);
            result.put("message", "上传失败");
            return result;
        }
    }

    // 获取全部房源
    @GetMapping
    public List<Homestay> getHomestays() {
        return homestayRepository.findAll();
    }

    // 新增房源
    @PostMapping
    public Map<String, Object> createHomestay(@RequestBody Homestay homestay) {
        Map<String, Object> result = new HashMap<>();

        if (homestay.getName() == null || homestay.getName().isBlank() || homestay.getPrice() == null) {
            result.put("code", 400);
            result.put("message", "房源名称和价格必填");
            return result;
        }

        Homestay saved = homestayRepository.save(homestay);
        result.put("code", 200);
        result.put("message", "创建成功");
        result.put("data", saved);
        return result;
    }

    // 编辑房源：已有有效预约时禁止
    @PutMapping("/{id}")
    public Map<String, Object> updateHomestay(@PathVariable Long id, @RequestBody Homestay payload) {
        Map<String, Object> result = new HashMap<>();
        Homestay homestay = homestayRepository.findById(id).orElse(null);

        if (homestay == null) {
            result.put("code", 404);
            result.put("message", "房源不存在");
            return result;
        }
        if (reservationRepository.existsByRoomIdAndStatusIn(id, ACTIVE_RESERVATION_STATUSES)) {
            result.put("code", 400);
            result.put("message", "该房型存在有效预约，禁止编辑");
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
        result.put("message", "更新成功");
        result.put("data", saved);
        return result;
    }

    // 删除房源：已有有效预约时禁止
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteHomestay(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        if (!homestayRepository.existsById(id)) {
            result.put("code", 404);
            result.put("message", "房源不存在");
            return result;
        }
        if (reservationRepository.existsByRoomIdAndStatusIn(id, ACTIVE_RESERVATION_STATUSES)) {
            result.put("code", 400);
            result.put("message", "该房型存在有效预约，禁止删除");
            return result;
        }

        homestayRepository.deleteById(id);
        result.put("code", 200);
        result.put("message", "删除成功");
        return result;
    }
}