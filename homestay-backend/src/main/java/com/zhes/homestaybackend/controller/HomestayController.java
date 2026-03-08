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

    @GetMapping("/api/homestays")
    public List<Homestay> getHomestays() {
        return homestayRepository.findAll();
    }

    @PostMapping("/api/reserve/submit")
    public Map<String, Object> submitReservation(@RequestBody Reservation reservation) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (reservation.getRoomId() == null || reservation.getUserId() == null || reservation.getDate() == null) {
                result.put("code", 400);
                result.put("message", "缺少必填字段");
                return result;
            }
            if (reservation.getPhone() == null || reservation.getPhone().isBlank()
                || reservation.getGuestName() == null || reservation.getGuestName().isBlank()
                || reservation.getIdCard() == null || reservation.getIdCard().isBlank()
                || reservation.getGender() == null || reservation.getGender().isBlank()
                || reservation.getCheckOutDate() == null || reservation.getCheckOutDate().isBlank()) {
                result.put("code", 400);
                result.put("message", "预约信息不完整");
                return result;
            }
            Homestay homestay = homestayRepository.findById(reservation.getRoomId()).orElse(null);
            if (homestay == null) {
                result.put("code", 404);
                result.put("message", "房源不存在");
                return result;
            }
            if (reservation.getRoomType() != null
                && !reservation.getRoomType().isBlank()
                && !reservation.getRoomType().equals(homestay.getName())) {
                result.put("code", 400);
                result.put("message", "房间类型与房源不一致");
                return result;
            }

            // Keep reservation.roomType consistent with homestay.name
            reservation.setRoomType(homestay.getName());
            reservation.setStatus("待确认");

            LocalDate checkInDate = LocalDate.parse(reservation.getDate());
            LocalDate checkOutDate = LocalDate.parse(reservation.getCheckOutDate());
            long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            if (days <= 0) {
                result.put("code", 400);
                result.put("message", "离店日期必须晚于入住日期");
                return result;
            }
            reservation.setStayDays((int) days);
            if (homestay.getPrice() == null || homestay.getPrice() < 0) {
                result.put("code", 400);
                result.put("message", "房源价格异常");
                return result;
            }

            // Always compute paid amount on backend to prevent tampering
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
