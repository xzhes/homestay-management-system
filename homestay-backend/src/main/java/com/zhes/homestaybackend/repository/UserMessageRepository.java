package com.zhes.homestaybackend.repository;

import com.zhes.homestaybackend.entity.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {
    List<UserMessage> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<UserMessage> findAllByOrderByCreatedAtDesc();
}
