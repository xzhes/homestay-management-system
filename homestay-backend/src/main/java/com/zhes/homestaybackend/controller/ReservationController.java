package com.zhes.homestaybackend.controller;

import com.zhes.homestaybackend.entity.Reservation;
import com.zhes.homestaybackend.repository.ReservationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public List<Reservation> getReservations(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return reservationRepository.findByUserIdOrderByIdDesc(userId);
        }
        return reservationRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteReservation(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        if (!reservationRepository.existsById(id)) {
            result.put("code", 404);
            result.put("message", "Reservation not found");
            return result;
        }
        reservationRepository.deleteById(id);
        result.put("code", 200);
        result.put("message", "Deleted");
        return result;
    }
}
