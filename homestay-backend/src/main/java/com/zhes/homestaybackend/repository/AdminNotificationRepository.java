package com.zhes.homestaybackend.repository;

import com.zhes.homestaybackend.entity.AdminNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Long> {
    List<AdminNotification> findByReadFlagFalseOrderByCreatedAtDesc();
}
