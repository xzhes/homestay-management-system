package com.zhes.homestaybackend.controller;

import com.zhes.homestaybackend.entity.User;
import com.zhes.homestaybackend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 后台用户管理接口
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 查询全部用户
    @GetMapping
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // 新增用户
    @PostMapping
    public Map<String, Object> createUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();

        if (user.getUsername() == null || user.getUsername().isBlank() || user.getPassword() == null || user.getPassword().isBlank()) {
            result.put("code", 400);
            result.put("message", "Username and password are required");
            return result;
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            result.put("code", 400);
            result.put("message", "Username already exists");
            return result;
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("guest");
        }

        User saved = userRepository.save(user);
        result.put("code", 200);
        result.put("message", "Created");
        result.put("data", saved);
        return result;
    }

    // 编辑用户信息
    @PutMapping("/{id}")
    public Map<String, Object> updateUser(@PathVariable Integer id, @RequestBody User payload) {
        Map<String, Object> result = new HashMap<>();
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            result.put("code", 404);
            result.put("message", "User not found");
            return result;
        }

        if (payload.getUsername() != null && !payload.getUsername().isBlank()) {
            user.setUsername(payload.getUsername());
        }
        if (payload.getPassword() != null && !payload.getPassword().isBlank()) {
            user.setPassword(payload.getPassword());
        }
        if (payload.getRole() != null && !payload.getRole().isBlank()) {
            user.setRole(payload.getRole());
        }

        User saved = userRepository.save(user);
        result.put("code", 200);
        result.put("message", "Updated");
        result.put("data", saved);
        return result;
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();

        if (!userRepository.existsById(id)) {
            result.put("code", 404);
            result.put("message", "User not found");
            return result;
        }

        userRepository.deleteById(id);
        result.put("code", 200);
        result.put("message", "Deleted");
        return result;
    }
}
