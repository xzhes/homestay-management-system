package com.zhes.homestaybackend.repository;

import com.zhes.homestaybackend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserIdOrderByIdDesc(Long userId);

    boolean existsByRoomIdAndStatusIn(Long roomId, Collection<String> statuses);

    boolean existsByRoomIdAndStatusInAndDateLessThanAndCheckOutDateGreaterThan(
        Long roomId,
        Collection<String> statuses,
        String checkOutDate,
        String date
    );
}
