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
        Map<String, Object> response = new HashMap<>();

        if (user.getUsername() == null || user.getUsername().isBlank()
            || user.getPassword() == null || user.getPassword().isBlank()) {
            response.put("code", 400);
            response.put("message", "请输入用户名和密码");
            response.put("data", null);
            return response;
        }

        User dbUser = repository.findByUsernameAndPassword(
            user.getUsername(),
            user.getPassword()
        );

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

    @PostMapping("/api/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (user.getUsername() == null || user.getUsername().isBlank()
            || user.getPassword() == null || user.getPassword().isBlank()) {
            response.put("code", 400);
            response.put("message", "用户名和密码不能为空");
            response.put("data", null);
            return response;
        }

        if (repository.existsByUsername(user.getUsername())) {
            response.put("code", 400);
            response.put("message", "用户名已存在");
            response.put("data", null);
            return response;
        }

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setRole("guest");

        User saved = repository.save(newUser);

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", saved.getId());
        userData.put("username", saved.getUsername());
        userData.put("role", saved.getRole());

        response.put("code", 200);
        response.put("message", "注册成功");
        response.put("data", userData);
        return response;
    }
}
