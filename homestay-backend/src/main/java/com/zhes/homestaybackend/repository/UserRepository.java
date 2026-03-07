package com.zhes.homestaybackend.repository;

import com.zhes.homestaybackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsernameAndPassword(String username, String password);

    boolean existsByUsername(String username);
}
