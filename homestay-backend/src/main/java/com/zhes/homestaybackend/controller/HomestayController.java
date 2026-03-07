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

            if (!homestayRepository.existsById(reservation.getRoomId())) {
                result.put("code", 404);
                result.put("message", "Homestay not found");
                return result;
            }

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