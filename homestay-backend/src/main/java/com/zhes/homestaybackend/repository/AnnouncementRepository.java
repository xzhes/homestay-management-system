package com.zhes.homestaybackend.repository;

import com.zhes.homestaybackend.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByStatusOrderByCreatedAtDesc(String status);

    List<Announcement> findAllByOrderByCreatedAtDesc();
}
