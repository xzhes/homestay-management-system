package com.zhes.homestaybackend.repository;


import com.zhes.homestaybackend.entity.Homestay;
import org.springframework.data.jpa.repository.JpaRepository;

// 房源表数据访问
public interface HomestayRepository extends JpaRepository<Homestay, Long> {
}
