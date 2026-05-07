package com.zhes.homestaybackend.controller;

import com.zhes.homestaybackend.entity.Reservation;
import com.zhes.homestaybackend.repository.HomestayAvailabilityRepository;
import com.zhes.homestaybackend.repository.ReservationRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Reservation management endpoints for admin and user actions
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final HomestayAvailabilityRepository homestayAvailabilityRepository;

    public ReservationController(ReservationRepository reservationRepository,
                                 HomestayAvailabilityRepository homestayAvailabilityRepository) {
        this.reservationRepository = reservationRepository;
        this.homestayAvailabilityRepository = homestayAvailabilityRepository;
    }

    @GetMapping
    public List<Reservation> getReservations(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return reservationRepository.findByUserIdOrderByIdDesc(userId);
        }
        return reservationRepository.findAll();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Map<String, Object> deleteReservation(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            result.put("code", 404);
            result.put("message", "预约不存在");
            return result;
        }

        homestayAvailabilityRepository.deleteByReservationId(id);
        reservationRepository.delete(reservation);
        result.put("code", 200);
        result.put("message", "删除成功");
        return result;
    }

    @PutMapping("/{id}/confirm")
    public Map<String, Object> confirmReservation(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            result.put("code", 404);
            result.put("message", "预约不存在");
            return result;
        }

        String status = reservation.getStatus();
        boolean canConfirm = "待确认".equals(status) || "已预订".equals(status) || "BOOKED".equals(status);
        if (!canConfirm) {
            result.put("code", 400);
            result.put("message", "当前状态不可确认");
            return result;
        }

        reservation.setStatus("待入住");
        reservationRepository.save(reservation);
        result.put("code", 200);
        result.put("message", "确认成功");
        return result;
    }

    @PutMapping("/{id}/check-in")
    public Map<String, Object> checkIn(@PathVariable Long id, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            result.put("code", 404);
            result.put("message", "预约不存在");
            return result;
        }
        if (!reservation.getUserId().equals(userId)) {
            result.put("code", 403);
            result.put("message", "无权限操作该预约");
            return result;
        }
        if (!"待入住".equals(reservation.getStatus())) {
            result.put("code", 400);
            result.put("message", "当前状态不可办理入住");
            return result;
        }

        reservation.setStatus("已入住");
        reservationRepository.save(reservation);
        result.put("code", 200);
        result.put("message", "入住成功");
        return result;
    }

    @PutMapping("/{id}/check-out")
    public Map<String, Object> checkOut(@PathVariable Long id, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            result.put("code", 404);
            result.put("message", "预约不存在");
            return result;
        }
        if (!reservation.getUserId().equals(userId)) {
            result.put("code", 403);
            result.put("message", "无权限操作该预约");
            return result;
        }
        if (!"已入住".equals(reservation.getStatus())) {
            result.put("code", 400);
            result.put("message", "当前状态不可办理退房");
            return result;
        }

        reservation.setStatus("已退房");
        reservationRepository.save(reservation);
        result.put("code", 200);
        result.put("message", "退房成功");
        return result;
    }
}
