package com.zhes.homestaybackend.controller;

import com.zhes.homestaybackend.entity.Homestay;
import com.zhes.homestaybackend.entity.HomestayAvailability;
import com.zhes.homestaybackend.entity.Reservation;
import com.zhes.homestaybackend.repository.HomestayAvailabilityRepository;
import com.zhes.homestaybackend.repository.HomestayRepository;
import com.zhes.homestaybackend.repository.ReservationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Frontend homestay list and reservation submit endpoints
@CrossOrigin(origins = "*")
@RestController
public class HomestayController {
    private static final List<String> ACTIVE_RESERVATION_STATUSES = List.of(
        "待确认", "待入住", "已入住", "已预订", "BOOKED", "CHECKED_IN"
    );

    private final HomestayRepository homestayRepository;
    private final ReservationRepository reservationRepository;
    private final HomestayAvailabilityRepository homestayAvailabilityRepository;

    public HomestayController(HomestayRepository homestayRepository,
                              ReservationRepository reservationRepository,
                              HomestayAvailabilityRepository homestayAvailabilityRepository) {
        this.homestayRepository = homestayRepository;
        this.reservationRepository = reservationRepository;
        this.homestayAvailabilityRepository = homestayAvailabilityRepository;
    }

    @GetMapping("/api/homestays")
    public List<Homestay> getHomestays() {
        return homestayRepository.findAll();
    }

    @PostMapping("/api/reserve/submit")
    @Transactional
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

            reservation.setRoomType(homestay.getName());
            reservation.setStatus("待确认");

            LocalDate checkInDate = LocalDate.parse(reservation.getDate());
            LocalDate checkOutDate = LocalDate.parse(reservation.getCheckOutDate());
            LocalDate today = LocalDate.now();
            if (checkInDate.isBefore(today)) {
                result.put("code", 400);
                result.put("message", "入住日期不能早于当前日期");
                return result;
            }
// 用 LocalDate.now() 校验，入住日期早于今天会直接返回
            long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            if (days <= 0) {
                result.put("code", 400);
                result.put("message", "离店日期必须晚于入住日期");
                return result;
            }

            LocalDate endDate = checkOutDate.minusDays(1);
            boolean conflictOnCalendar = homestayAvailabilityRepository.existsByRoomIdAndStayDateBetween(
                reservation.getRoomId(), checkInDate, endDate
            );
            boolean conflictOnLegacyData =
                reservationRepository.existsByRoomIdAndStatusInAndDateLessThanAndCheckOutDateGreaterThan(
                    reservation.getRoomId(),
                    ACTIVE_RESERVATION_STATUSES,
                    reservation.getCheckOutDate(),
                    reservation.getDate()
                );
            if (conflictOnCalendar || conflictOnLegacyData) {
                result.put("code", 409);
                result.put("message", "该选定日期已有预约，请更换日期或房型");
                return result;
            }

            reservation.setStayDays((int) days);

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

            Reservation savedReservation = reservationRepository.save(reservation);

            List<HomestayAvailability> availabilityList = new ArrayList<>();
            for (LocalDate current = checkInDate; current.isBefore(checkOutDate); current = current.plusDays(1)) {
                HomestayAvailability availability = new HomestayAvailability();
                availability.setRoomId(savedReservation.getRoomId());
                availability.setReservationId(savedReservation.getId());
                availability.setStayDate(current);
                availabilityList.add(availability);
            }
            homestayAvailabilityRepository.saveAll(availabilityList);
            homestayAvailabilityRepository.flush();

            result.put("code", 200);
            result.put("message", "success");
            return result;
        } catch (DataIntegrityViolationException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            result.put("code", 409);
            result.put("message", "该选定日期已有预约，请更换日期或房型");
            return result;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            result.put("code", 500);
            result.put("message", "fail");
            return result;
        }
    }
}
