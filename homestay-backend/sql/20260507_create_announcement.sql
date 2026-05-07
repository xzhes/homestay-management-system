CREATE TABLE IF NOT EXISTS announcement (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  content TEXT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'published',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  INDEX idx_announcement_status_created_at (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO announcement (title, content, status)
VALUES
  ('入住温馨提示', '请在预约成功后按预约日期办理入住，如需取消预约请提前在“我的预约”中操作。', 'published'),
  ('平台公告', '民宿预约管理系统已开放线上预约功能，欢迎查看房源并提交预约。', 'published');
