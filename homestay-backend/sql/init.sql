CREATE DATABASE IF NOT EXISTS homestay_db DEFAULT CHARACTER SET utf8mb4;
USE homestay_db;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL
);

-- 房源表（含图片地址）
CREATE TABLE IF NOT EXISTS homestay (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  description VARCHAR(255),
  image_url VARCHAR(255)
);

-- 兼容旧库补字段
ALTER TABLE homestay ADD COLUMN IF NOT EXISTS image_url VARCHAR(255);

-- 预约表
CREATE TABLE IF NOT EXISTS reservation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  room_id BIGINT NOT NULL,
  date VARCHAR(20) NOT NULL
);

-- 初始化账号
INSERT IGNORE INTO `user` (id, username, password, role) VALUES
  (1, 'admin', '123456', 'admin'),
  (2, 'guest', '123456', 'guest');

-- 初始化房源
INSERT IGNORE INTO homestay (id, name, price, description, image_url) VALUES
  (1, '海景别墅', 888.00, '面朝大海，春暖花开，豪华装修', 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=80'),
  (2, '山景小屋', 388.00, '山间静谧，适合度假放松', 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=1200&q=80'),
  (3, '城市公寓', 288.00, '市中心位置，交通便利', 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1200&q=80'),
  (4, '田园小院', 488.00, '体验乡村生活，感受田园风光', ''),
  (5, '豪华大酒店', 666.00, '潍坊豪华酒店', '');
