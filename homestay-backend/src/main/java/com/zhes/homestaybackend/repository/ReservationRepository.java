package com.zhes.homestaybackend.repository;

import com.zhes.homestaybackend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

// 预约表数据访问
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // 按用户查询预约（倒序）
    List<Reservation> findByUserIdOrderByIdDesc(Long userId);
    // 判断房源是否存在指定状态的预约
    boolean existsByRoomIdAndStatusIn(Long roomId, Collection<String> statuses);
}
