CREATE DATABASE IF NOT EXISTS homestay_db DEFAULT CHARACTER SET utf8mb4;
USE homestay_db;

CREATE TABLE IF NOT EXISTS `user` (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS homestay (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS reservation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  room_id BIGINT NOT NULL,
  date VARCHAR(20) NOT NULL
);

INSERT IGNORE INTO `user` (id, username, password, role) VALUES
  (1, 'admin', '123456', 'admin'),
  (2, 'guest', '123456', 'guest');

INSERT IGNORE INTO homestay (id, name, price, description) VALUES
  (1, '山景双人房', 268.00, '近景区，含早餐'),
  (2, '湖景家庭房', 488.00, '适合亲子，含停车位'),
  (3, '庭院大床房', 328.00, '独立小院，安静舒适');
