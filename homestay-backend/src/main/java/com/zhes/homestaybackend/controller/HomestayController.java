package com.zhes.homestaybackend.controller;

import com.zhes.homestaybackend.entity.Homestay;
import com.zhes.homestaybackend.entity.Reservation;
import com.zhes.homestaybackend.repository.HomestayRepository;
import com.zhes.homestaybackend.repository.ReservationRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 前台房源列表 + 预约提交
@CrossOrigin(origins = "*")
@RestController
public class HomestayController {

    private final HomestayRepository homestayRepository;
    private final ReservationRepository reservationRepository;

    public HomestayController(HomestayRepository homestayRepository,
                              ReservationRepository reservationRepository) {
        this.homestayRepository = homestayRepository;
        this.reservationRepository = reservationRepository;
    }

    // 用户端获取全部房源
    @GetMapping("/api/homestays")
    public List<Homestay> getHomestays() {
        return homestayRepository.findAll();
    }

    // 用户端提交预约
    @PostMapping("/api/reserve/submit")
    public Map<String, Object> submitReservation(@RequestBody Reservation reservation) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 基础必填字段
            if (reservation.getRoomId() == null || reservation.getUserId() == null || reservation.getDate() == null) {
                result.put("code", 400);
                result.put("message", "缺少必填字段");
                return result;
            }
            // 预约明细必填
            if (reservation.getPhone() == null || reservation.getPhone().isBlank()
                || reservation.getGuestName() == null || reservation.getGuestName().isBlank()
                || reservation.getIdCard() == null || reservation.getIdCard().isBlank()
                || reservation.getGender() == null || reservation.getGender().isBlank()
                || reservation.getCheckOutDate() == null || reservation.getCheckOutDate().isBlank()) {
                result.put("code", 400);
                result.put("message", "预约信息不完整");
                return result;
            }
            // 房源必须存在
            Homestay homestay = homestayRepository.findById(reservation.getRoomId()).orElse(null);
            if (homestay == null) {
                result.put("code", 404);
                result.put("message", "房源不存在");
                return result;
            }
            // 如果前端带了房型，需与房源名称一致
            if (reservation.getRoomType() != null
                && !reservation.getRoomType().isBlank()
                && !reservation.getRoomType().equals(homestay.getName())) {
                result.put("code", 400);
                result.put("message", "房间类型与房源不一致");
                return result;
            }

            // 统一使用房源名称作为房型，防止不一致
            reservation.setRoomType(homestay.getName());
            reservation.setStatus("待确认");

            // 计算入住天数（离店 - 入住）
            LocalDate checkInDate = LocalDate.parse(reservation.getDate());
            LocalDate checkOutDate = LocalDate.parse(reservation.getCheckOutDate());
            long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            if (days <= 0) {
                result.put("code", 400);
                result.put("message", "离店日期必须晚于入住日期");
                return result;
            }
            reservation.setStayDays((int) days);

            // 计算实付金额：房价 * 天数（后端计算，防止篡改）
            if (homestay.getPrice() == null || homestay.getPrice() < 0) {
                result.put("code", 400);
                result.put("message", "房源价格异常");
                return result;
            }

            double paid = BigDecimal.valueOf(homestay.getPrice())
                .multiply(BigDecimal.valueOf(days))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
            reservation.setPaidAmount(paid);

            reservationRepository.save(reservation);
            result.put("code", 200);
            result.put("message", "success");
            return result;
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "fail");
            return result;
        }
    }
}