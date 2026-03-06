package com.zhes.homestaybackend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")  //允许任何前端来访问  夸端口：允许
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "homestay backend is running";
    }
}

//@RestController
//public class TestController {
//
//    @GetMapping("/test")
//    public String test() {
//        return "homestay backend is running";
//    }
//}