package com.zhes.homestaybackend.repository;

import com.zhes.homestaybackend.entity.HomestayAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface HomestayAvailabilityRepository extends JpaRepository<HomestayAvailability, Long> {
    boolean existsByRoomIdAndStayDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);

    void deleteByReservationId(Long reservationId);
}
