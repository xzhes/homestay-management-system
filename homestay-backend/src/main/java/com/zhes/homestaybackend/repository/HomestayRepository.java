package com.zhes.homestaybackend.repository;


import com.zhes.homestaybackend.entity.Homestay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomestayRepository extends JpaRepository<Homestay, Long> {
}