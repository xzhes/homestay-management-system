package com.zhes.homestaybackend.controller;

import com.zhes.homestaybackend.entity.User;
import com.zhes.homestaybackend.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestBody User user, HttpSession session) {

        // ⭐ 调试信息 - 看看收到了什么
        System.out.println("===== 登录调试开始 =====");
        System.out.println("收到的用户名: " + user.getUsername());
        System.out.println("收到的密码: " + user.getPassword());

        User dbUser = repository.findByUsernameAndPassword(
                user.getUsername(),
                user.getPassword()
        );

        // ⭐ 调试信息 - 看看查询结果
        if (dbUser != null) {
            System.out.println("✅ 找到用户！");
            System.out.println("用户ID: " + dbUser.getId());
            System.out.println("用户名: " + dbUser.getUsername());
            System.out.println("用户角色: " + dbUser.getRole());
        } else {
            System.out.println("❌ 没找到用户！");
        }
        System.out.println("===== 登录调试结束 =====");

        Map<String, Object> response = new HashMap<>();

        if (dbUser != null) {
            session.setAttribute("user", dbUser);

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", dbUser.getId());
            userData.put("username", dbUser.getUsername());
            userData.put("role", dbUser.getRole());

            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("data", userData);
        } else {
            response.put("code", 400);
            response.put("message", "用户名或密码错误");
            response.put("data", null);
        }

        return response;
    }
}