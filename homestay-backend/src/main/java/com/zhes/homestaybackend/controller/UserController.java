package com.zhes.homestaybackend.controller;

import com.zhes.homestaybackend.entity.User;
import com.zhes.homestaybackend.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/api/login")
    public String login(@RequestBody User user, HttpSession session) {

        User dbUser = repository.findByUsernameAndPassword(
                user.getUsername(),
                user.getPassword()
        );

        if (dbUser != null) {
            session.setAttribute("user", dbUser);
            return "success";
        } else {
            return "fail";
        }
    }
}
