package com.zhes.homestaybackend.entity;

import jakarta.persistence.*;

@Entity
public class Homestay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 房源名称
    private String name;
    // 房源价格（元/晚）
    private Double price;
    // 房源描述
    private String description;
    // 房源图片地址（支持 http 链接或 /uploads/xxx.jpg）
    private String imageUrl;

    // 空构造函数
    public Homestay() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
