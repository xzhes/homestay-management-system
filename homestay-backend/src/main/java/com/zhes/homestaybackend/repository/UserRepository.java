package com.zhes.homestaybackend.repository;

import com.zhes.homestaybackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 用户表数据访问
public interface UserRepository extends JpaRepository<User, Integer> {

    // 登录查询（用户名 + 密码）
    User findByUsernameAndPassword(String username, String password);

    // 注册校验：用户名是否存在
    boolean existsByUsername(String username);
}
