-- =====================================================
-- 班级事务与学风管理系统 - 数据库初始化脚本
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(50) UNIQUE COMMENT '角色编码',
  `description` VARCHAR(200) COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ----------------------------
-- 2. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) COMMENT '手机号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `role_id` INT COMMENT '角色ID',
  `class_id` BIGINT COMMENT '所属班级ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0禁用/1启用)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 3. 班级表
-- ----------------------------
DROP TABLE IF EXISTS `class_info`;
CREATE TABLE `class_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `class_name` VARCHAR(100) NOT NULL COMMENT '班级名称',
  `grade` VARCHAR(20) NOT NULL COMMENT '年级',
  `major` VARCHAR(100) COMMENT '专业',
  `department` VARCHAR(100) COMMENT '院系',
  `student_count` INT DEFAULT 0 COMMENT '学生人数',
  `teacher_id` BIGINT COMMENT '班主任ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';


-- ----------------------------
-- 4. 学生表
-- ----------------------------
DROP TABLE IF EXISTS `student_info`;
CREATE TABLE `student_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
  `user_id` BIGINT UNIQUE COMMENT '关联用户ID',
  `class_id` BIGINT COMMENT '班级ID',
  `gender` CHAR(1) COMMENT '性别(M/F)',
  `enrollment_date` DATE COMMENT '入学日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- ----------------------------
-- 5. 课程表
-- ----------------------------
DROP TABLE IF EXISTS `course_info`;
CREATE TABLE `course_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
  `course_code` VARCHAR(50) UNIQUE COMMENT '课程代码',
  `teacher_id` BIGINT COMMENT '任课教师ID',
  `credit` INT COMMENT '学分',
  `semester` VARCHAR(20) COMMENT '学期',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- ----------------------------
-- 6. 考勤记录表
-- ----------------------------
DROP TABLE IF EXISTS `attendance_record`;
CREATE TABLE `attendance_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `attendance_date` DATE NOT NULL COMMENT '考勤日期',
  `status` TINYINT NOT NULL COMMENT '状态(0缺勤/1出勤/2迟到/3早退/4请假)',
  `remark` VARCHAR(200) COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` BIGINT COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_attendance_date` (`attendance_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考勤记录表';

-- ----------------------------
-- 7. 作业表
-- ----------------------------
DROP TABLE IF EXISTS `assignment_info`;
CREATE TABLE `assignment_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `title` VARCHAR(200) NOT NULL COMMENT '作业标题',
  `description` TEXT COMMENT '作业描述',
  `deadline` DATETIME NOT NULL COMMENT '截止时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0关闭/1开放)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` BIGINT COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业表';

-- ----------------------------
-- 8. 作业提交表
-- ----------------------------
DROP TABLE IF EXISTS `assignment_submit`;
CREATE TABLE `assignment_submit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `assignment_id` BIGINT NOT NULL COMMENT '作业ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `status` TINYINT DEFAULT 0 COMMENT '状态(0未提交/1已提交/2逾期提交)',
  `submit_time` DATETIME COMMENT '提交时间',
  `remark` VARCHAR(200) COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_assignment_id` (`assignment_id`),
  KEY `idx_student_id` (`student_id`),
  UNIQUE KEY `uk_assignment_student` (`assignment_id`, `student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业提交表';

-- ----------------------------
-- 9. 成绩表
-- ----------------------------
DROP TABLE IF EXISTS `grade_info`;
CREATE TABLE `grade_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `grade_type` TINYINT NOT NULL COMMENT '类型(1平时/2期中/3期末)',
  `score` DECIMAL(5,2) NOT NULL COMMENT '分数',
  `semester` VARCHAR(20) COMMENT '学期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` BIGINT COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩表';


-- ----------------------------
-- 10. 预警记录表
-- ----------------------------
DROP TABLE IF EXISTS `warning_record`;
CREATE TABLE `warning_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `warning_type` TINYINT NOT NULL COMMENT '类型(1作业未交/2出勤率低/3挂科风险)',
  `title` VARCHAR(200) NOT NULL COMMENT '预警标题',
  `content` TEXT COMMENT '预警内容',
  `status` TINYINT DEFAULT 0 COMMENT '状态(0未处理/1已处理/2已忽略)',
  `handler_id` BIGINT COMMENT '处理人ID',
  `handle_result` TEXT COMMENT '处理结果',
  `handle_time` DATETIME COMMENT '处理时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警记录表';

-- ----------------------------
-- 11. 班委信息表
-- ----------------------------
DROP TABLE IF EXISTS `class_committee`;
CREATE TABLE `class_committee` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `class_id` BIGINT NOT NULL COMMENT '班级ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `position` VARCHAR(50) NOT NULL COMMENT '职务',
  `start_date` DATE COMMENT '任职开始日期',
  `end_date` DATE COMMENT '任职结束日期',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  PRIMARY KEY (`id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班委信息表';

-- ----------------------------
-- 12. 班会记录表
-- ----------------------------
DROP TABLE IF EXISTS `class_meeting`;
CREATE TABLE `class_meeting` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `class_id` BIGINT NOT NULL COMMENT '班级ID',
  `title` VARCHAR(200) NOT NULL COMMENT '班会主题',
  `content` TEXT COMMENT '班会内容',
  `meeting_time` DATETIME NOT NULL COMMENT '班会时间',
  `location` VARCHAR(100) COMMENT '地点',
  `recorder_id` BIGINT COMMENT '记录人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班会记录表';

-- ----------------------------
-- 13. 通知公告表
-- ----------------------------
DROP TABLE IF EXISTS `notice_info`;
CREATE TABLE `notice_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `notice_type` TINYINT NOT NULL COMMENT '类型(1缴费/2评优/3活动/4其他)',
  `publisher_id` BIGINT COMMENT '发布人ID',
  `class_id` BIGINT COMMENT '目标班级ID(NULL表示全部)',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0撤回/1发布)',
  `publish_time` DATETIME COMMENT '发布时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_publisher_id` (`publisher_id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告表';

-- ----------------------------
-- 14. 通知阅读记录表
-- ----------------------------
DROP TABLE IF EXISTS `notice_read`;
CREATE TABLE `notice_read` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `notice_id` BIGINT NOT NULL COMMENT '通知ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `read_time` DATETIME COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  KEY `idx_notice_id` (`notice_id`),
  KEY `idx_user_id` (`user_id`),
  UNIQUE KEY `uk_notice_user` (`notice_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知阅读记录表';

-- ----------------------------
-- 15. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT COMMENT '操作用户ID',
  `username` VARCHAR(50) COMMENT '用户名',
  `operation_type` VARCHAR(50) COMMENT '操作类型',
  `module` VARCHAR(50) COMMENT '操作模块',
  `content` TEXT COMMENT '操作内容',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ----------------------------
-- 16. 课程学生关联表
-- ----------------------------
DROP TABLE IF EXISTS `course_student`;
CREATE TABLE `course_student` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_student_id` (`student_id`),
  UNIQUE KEY `uk_course_student` (`course_id`, `student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程学生关联表';

SET FOREIGN_KEY_CHECKS = 1;


-- =====================================================
-- 预置数据
-- =====================================================

-- ----------------------------
-- 预置角色数据
-- ----------------------------
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`) VALUES
(1, '系统管理员', 'ADMIN', '系统最高权限，可管理所有功能'),
(2, '班主任', 'CLASS_TEACHER', '班级管理权限，负责班级事务和学风监控'),
(3, '任课教师', 'COURSE_TEACHER', '课程管理权限，负责考勤、作业和成绩管理'),
(4, '学生', 'STUDENT', '学生查看权限，可查看个人学业数据和接收通知');


-- ----------------------------
-- 预置测试账号数据
-- 密码: 123456 (BCrypt加密后)
-- ----------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `role_id`, `status`) VALUES
(1, 'admin', '$2a$10$vKcoRoEjFwol2R5o6bKI5uRL7BLxd1mS/Ir02IRrYmzxEFzAwARay', '系统管理员', '13800000001', 'admin@example.com', 1, 1),
(2, 'teacher01', '$2a$10$vKcoRoEjFwol2R5o6bKI5uRL7BLxd1mS/Ir02IRrYmzxEFzAwARay', '张老师', '13800000002', 'teacher01@example.com', 2, 1),
(3, 'course01', '$2a$10$vKcoRoEjFwol2R5o6bKI5uRL7BLxd1mS/Ir02IRrYmzxEFzAwARay', '李老师', '13800000003', 'course01@example.com', 3, 1),
(4, 'student01', '$2a$10$vKcoRoEjFwol2R5o6bKI5uRL7BLxd1mS/Ir02IRrYmzxEFzAwARay', '王小明', '13800000004', 'student01@example.com', 4, 1),
(5, 'student02', '$2a$10$vKcoRoEjFwol2R5o6bKI5uRL7BLxd1mS/Ir02IRrYmzxEFzAwARay', '李小红', '13800000005', 'student02@example.com', 4, 1),
(6, 'student03', '$2a$10$vKcoRoEjFwol2R5o6bKI5uRL7BLxd1mS/Ir02IRrYmzxEFzAwARay', '张小华', '13800000006', 'student03@example.com', 4, 1),
(7, 'student04', '$2a$10$vKcoRoEjFwol2R5o6bKI5uRL7BLxd1mS/Ir02IRrYmzxEFzAwARay', '刘小强', '13800000007', 'student04@example.com', 4, 1),
(8, 'student05', '$2a$10$vKcoRoEjFwol2R5o6bKI5uRL7BLxd1mS/Ir02IRrYmzxEFzAwARay', '陈小芳', '13800000008', 'student05@example.com', 4, 1);


-- ----------------------------
-- 预置班级数据
-- ----------------------------
INSERT INTO `class_info` (`id`, `class_name`, `grade`, `major`, `department`, `student_count`, `teacher_id`, `status`) VALUES
(1, '计算机2101班', '2021级', '计算机科学与技术', '信息工程学院', 5, 2, 1),
(2, '软件工程2101班', '2021级', '软件工程', '信息工程学院', 0, 2, 1);

-- 更新学生所属班级
UPDATE `sys_user` SET `class_id` = 1 WHERE `id` IN (4, 5, 6, 7, 8);

-- ----------------------------
-- 预置学生数据
-- ----------------------------
INSERT INTO `student_info` (`id`, `student_no`, `user_id`, `class_id`, `gender`, `enrollment_date`) VALUES
(1, '2021001001', 4, 1, 'M', '2021-09-01'),
(2, '2021001002', 5, 1, 'F', '2021-09-01'),
(3, '2021001003', 6, 1, 'M', '2021-09-01'),
(4, '2021001004', 7, 1, 'M', '2021-09-01'),
(5, '2021001005', 8, 2, 'F', '2021-09-01');

-- ----------------------------
-- 预置课程数据
-- ----------------------------
INSERT INTO `course_info` (`id`, `course_name`, `course_code`, `teacher_id`, `credit`, `semester`, `status`) VALUES
(1, '高等数学', 'MATH101', 3, 4, '2024-2025-1', 1),
(2, '数据结构', 'CS201', 3, 3, '2024-2025-1', 1),
(3, '操作系统', 'CS301', 3, 3, '2024-2025-1', 1);

-- ----------------------------
-- 预置课程学生关联数据
-- ----------------------------
INSERT INTO `course_student` (`course_id`, `student_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5),
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5);

-- ----------------------------
-- 预置考勤数据
-- ----------------------------
INSERT INTO `attendance_record` (`student_id`, `course_id`, `attendance_date`, `status`, `create_by`) VALUES
(1, 1, '2024-09-02', 1, 3), (2, 1, '2024-09-02', 1, 3), (3, 1, '2024-09-02', 2, 3), (4, 1, '2024-09-02', 1, 3), (5, 1, '2024-09-02', 1, 3),
(1, 1, '2024-09-09', 1, 3), (2, 1, '2024-09-09', 1, 3), (3, 1, '2024-09-09', 1, 3), (4, 1, '2024-09-09', 0, 3), (5, 1, '2024-09-09', 1, 3),
(1, 2, '2024-09-03', 1, 3), (2, 2, '2024-09-03', 1, 3), (3, 2, '2024-09-03', 1, 3), (4, 2, '2024-09-03', 1, 3), (5, 2, '2024-09-03', 2, 3);

-- ----------------------------
-- 预置作业数据
-- ----------------------------
INSERT INTO `assignment_info` (`id`, `course_id`, `title`, `description`, `deadline`, `status`, `create_by`) VALUES
(1, 1, '高等数学第一章作业', '完成课本第一章习题1-20', '2024-09-15 23:59:59', 1, 3),
(2, 1, '高等数学第二章作业', '完成课本第二章习题1-15', '2024-09-22 23:59:59', 1, 3),
(3, 2, '数据结构实验一', '实现顺序表的基本操作', '2024-09-20 23:59:59', 1, 3);

-- ----------------------------
-- 预置作业提交数据
-- ----------------------------
INSERT INTO `assignment_submit` (`assignment_id`, `student_id`, `status`, `submit_time`) VALUES
(1, 1, 1, '2024-09-14 20:30:00'), (1, 2, 1, '2024-09-15 10:00:00'), (1, 3, 1, '2024-09-15 22:00:00'), (1, 4, 0, NULL), (1, 5, 1, '2024-09-14 18:00:00'),
(2, 1, 1, '2024-09-21 15:00:00'), (2, 2, 1, '2024-09-20 20:00:00'), (2, 3, 0, NULL), (2, 4, 0, NULL), (2, 5, 1, '2024-09-22 10:00:00'),
(3, 1, 1, '2024-09-19 16:00:00'), (3, 2, 1, '2024-09-18 14:00:00'), (3, 3, 1, '2024-09-20 22:00:00'), (3, 4, 0, NULL), (3, 5, 1, '2024-09-19 20:00:00');

-- ----------------------------
-- 预置成绩数据
-- ----------------------------
INSERT INTO `grade_info` (`student_id`, `course_id`, `grade_type`, `score`, `semester`, `create_by`) VALUES
(1, 1, 1, 85.00, '2024-2025-1', 3), (2, 1, 1, 92.00, '2024-2025-1', 3), (3, 1, 1, 78.00, '2024-2025-1', 3), (4, 1, 1, 55.00, '2024-2025-1', 3), (5, 1, 1, 88.00, '2024-2025-1', 3),
(1, 2, 1, 90.00, '2024-2025-1', 3), (2, 2, 1, 88.00, '2024-2025-1', 3), (3, 2, 1, 75.00, '2024-2025-1', 3), (4, 2, 1, 50.00, '2024-2025-1', 3), (5, 2, 1, 82.00, '2024-2025-1', 3);

-- ----------------------------
-- 预置通知数据
-- ----------------------------
INSERT INTO `notice_info` (`id`, `title`, `content`, `notice_type`, `publisher_id`, `class_id`, `is_top`, `status`, `publish_time`) VALUES
(1, '关于缴纳2024-2025学年学费的通知', '请各位同学于2024年9月30日前完成学费缴纳，具体金额请查看财务系统。', 1, 2, 1, 1, 1, '2024-09-01 10:00:00'),
(2, '2024年国家奖学金评选通知', '根据学校安排，现启动2024年国家奖学金评选工作，请符合条件的同学积极申报。', 2, 2, 1, 0, 1, '2024-09-05 14:00:00'),
(3, '班级篮球赛报名通知', '学院将于10月举办班级篮球赛，请有意参加的同学在班群内报名。', 3, 2, 1, 0, 1, '2024-09-10 09:00:00');

-- ----------------------------
-- 预置班委数据
-- ----------------------------
INSERT INTO `class_committee` (`class_id`, `student_id`, `position`, `start_date`, `status`) VALUES
(1, 1, '班长', '2021-09-15', 1),
(1, 2, '学习委员', '2021-09-15', 1),
(1, 5, '生活委员', '2021-09-15', 1);

-- ----------------------------
-- 预置班会记录数据
-- ----------------------------
INSERT INTO `class_meeting` (`class_id`, `title`, `content`, `meeting_time`, `location`, `recorder_id`) VALUES
(1, '新学期开学班会', '1. 介绍本学期课程安排\n2. 强调学风建设重要性\n3. 布置近期工作', '2024-09-02 19:00:00', '教学楼A301', 2),
(1, '国庆假期安全教育班会', '1. 假期安全注意事项\n2. 疫情防控要求\n3. 假期作业布置', '2024-09-28 19:00:00', '教学楼A301', 2);

-- ----------------------------
-- 预置预警数据
-- ----------------------------
INSERT INTO `warning_record` (`student_id`, `warning_type`, `title`, `content`, `status`) VALUES
(4, 1, '作业长期未交预警', '该学生连续3次未提交作业，请及时关注。', 0),
(4, 3, '挂科风险预警', '该学生多门课程成绩不及格，存在挂科风险。', 0);
