-- ================================================
-- 创建数据库与用户
-- ================================================

-- （1）创建数据库，如果不存在就创建
CREATE DATABASE IF NOT EXISTS `e_dove_user`
    -- 设置默认字符集为 utf8mb4
    DEFAULT CHARACTER SET utf8mb4
    -- 设置默认校对规则为 utf8mb4_unicode_ci
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- （2）删除已存在的用户（如果存在）
DROP USER IF EXISTS 'e_dove_user_admin'@'%';

-- （3）创建专门的用户，用于该数据库管理
CREATE USER IF NOT EXISTS 'e_dove_user_admin'@'%'
    IDENTIFIED BY 'eDoveMysql1014';

-- （4）授予该用户对数据库 e_dove_user 的所有权限
GRANT ALL PRIVILEGES
    ON `e_dove_user`.* -- 数据库名 + 所有表
    TO 'e_dove_user_admin'@'%';

-- （5）使权限变更立即生效
FLUSH PRIVILEGES;


-- ================================================
-- 创建表 并 初始化表数据
-- ================================================
USE e_dove_user;

-- 创建用户表
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`
(
    `user_id`     BIGINT       NOT NULL COMMENT '用户唯一标识ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `phone`       VARCHAR(20)  NOT NULL COMMENT '手机号码',
    `email`       VARCHAR(100) NULL COMMENT '电子邮箱',
    `avatar_url`  VARCHAR(255) NULL COMMENT '头像图片URL地址',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='存储系统所有用户的基本信息，包括用户、驿站工作人员、系统管理员等';

-- 创建用户地址表
-- DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`
(
    `address_id`     BIGINT       NOT NULL COMMENT '地址唯一标识ID',
    `user_id`        BIGINT       NOT NULL COMMENT '关联的用户ID',
    `receiver_name`  VARCHAR(50)  NOT NULL COMMENT '收件人姓名',
    `receiver_phone` VARCHAR(20)  NOT NULL COMMENT '收件人手机号',
    `country`        VARCHAR(50)  NOT NULL DEFAULT '中国' COMMENT '国家',
    `province`       VARCHAR(50)  NOT NULL COMMENT '省份',
    `city`           VARCHAR(50)  NOT NULL COMMENT '城市',
    `district`       VARCHAR(50)  NOT NULL COMMENT '区/县',
    `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
    `postal_code`    VARCHAR(10)  NULL COMMENT '邮政编码（可选）',
    `is_default`     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`address_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='存储用户的收货地址信息，支持设置默认地址';