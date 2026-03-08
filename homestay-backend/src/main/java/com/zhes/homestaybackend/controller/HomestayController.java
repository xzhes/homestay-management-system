package com.zhes.homestaybackend.controller;

import com.zhes.homestaybackend.entity.Homestay;
import com.zhes.homestaybackend.entity.Reservation;
import com.zhes.homestaybackend.repository.HomestayRepository;
import com.zhes.homestaybackend.repository.ReservationRepository;
import org.springframework.web.bind.annotation.*;

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
                result.put("message", "Missing required fields");
                return result;
            }
            if (reservation.getPhone() == null || reservation.getPhone().isBlank()
                || reservation.getIdCard() == null || reservation.getIdCard().isBlank()
                || reservation.getGender() == null || reservation.getGender().isBlank()
                || reservation.getRoomNumber() == null || reservation.getRoomNumber().isBlank()
                || reservation.getStayDays() == null || reservation.getStayDays() <= 0
                || reservation.getStatus() == null || reservation.getStatus().isBlank()) {
                result.put("code", 400);
                result.put("message", "Missing reservation details");
                return result;
            }
            if (reservation.getPaidAmount() == null || reservation.getPaidAmount() < 0) {
                result.put("code", 400);
                result.put("message", "Invalid paid amount");
                return result;
            }

            Homestay homestay = homestayRepository.findById(reservation.getRoomId()).orElse(null);
            if (homestay == null) {
                result.put("code", 404);
                result.put("message", "Homestay not found");
                return result;
            }
            if (reservation.getRoomType() != null
                && !reservation.getRoomType().isBlank()
                && !reservation.getRoomType().equals(homestay.getName())) {
                result.put("code", 400);
                result.put("message", "Room type must match homestay name");
                return result;
            }

            // Keep reservation.roomType consistent with homestay.name
            reservation.setRoomType(homestay.getName());

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
