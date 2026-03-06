package com.zhes.homestaybackend.controller;

import com.zhes.homestaybackend.entity.Homestay;
import com.zhes.homestaybackend.entity.Reservation;
import com.zhes.homestaybackend.repository.HomestayRepository;
import com.zhes.homestaybackend.repository.ReservationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
// 删除这一行 → @RequestMapping("/api")
public class HomestayController {

    private final HomestayRepository homestayRepository;
    private final ReservationRepository reservationRepository;

    public HomestayController(HomestayRepository homestayRepository,
                              ReservationRepository reservationRepository) {
        this.homestayRepository = homestayRepository;
        this.reservationRepository = reservationRepository;
    }

    // 获取民宿列表
    @GetMapping("/api/homestays")  // 改成完整路径
    public List<Homestay> getHomestays() {
        return homestayRepository.findAll();
    }

    // 提交预约
    @PostMapping("/api/reserve/submit")  // 改成完整路径
    public String submitReservation(@RequestBody Reservation reservation) {
        try {
            reservationRepository.save(reservation);
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }
}