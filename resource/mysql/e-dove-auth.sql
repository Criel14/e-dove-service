-- ================================================
-- 创建数据库与用户
-- ================================================

-- （1）创建数据库，如果不存在就创建
CREATE DATABASE IF NOT EXISTS `e_dove_auth`
    -- 设置默认字符集为 utf8mb4
    DEFAULT CHARACTER SET utf8mb4
    -- 设置默认校对规则为 utf8mb4_unicode_ci
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- （2）删除已存在的用户（如果存在）
DROP USER IF EXISTS 'e_dove_auth_admin'@'%';

-- （3）创建专门的用户，用于该数据库管理
CREATE USER IF NOT EXISTS 'e_dove_auth_admin'@'%'
    IDENTIFIED BY 'eDoveMysql1014';

-- （4）授予该用户对数据库 e_dove_auth 的所有权限
GRANT ALL PRIVILEGES
    ON `e_dove_auth`.* -- 数据库名 + 所有表
    TO 'e_dove_auth_admin'@'%';

-- （5）使权限变更立即生效
FLUSH PRIVILEGES;


-- ================================================
-- 创建表 并 初始化表数据
-- ================================================
USE e_dove_auth;

-- 创建用户认证表
-- DROP TABLE IF EXISTS `user_auth`;
CREATE TABLE `user_auth`
(
    `user_id`         BIGINT       NOT NULL COMMENT '用户唯一标识ID（对应）',
    `username`        VARCHAR(50)  NOT NULL COMMENT '用户名',
    `phone`           VARCHAR(20)  NOT NULL COMMENT '手机号码',
    `email`           VARCHAR(100) NULL COMMENT '电子邮箱',
    `password`        VARCHAR(255) NULL COMMENT '加密后的密码',
    `status`          TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '账户状态：1-正常，0-注销',
    `last_login_time` DATETIME     NULL COMMENT '最后登录时间',
    `create_time`     DATETIME     NOT NULL COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL COMMENT '最后更新时间',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`)
) COMMENT ='用户认证表，存储登录凭证和状态';

-- 创建角色表
-- DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `role_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色唯一标识ID',
    `role_name`   VARCHAR(50)  NOT NULL COMMENT '角色名称：普通用户、驿站管理员等',
    `role_desc`   VARCHAR(255) NULL COMMENT '角色描述',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL COMMENT '最后更新时间',
    PRIMARY KEY (`role_id`),
    UNIQUE KEY `uk_role_name` (`role_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='定义系统中的角色类型';
-- 插入数据
INSERT INTO `role` (`role_name`, `role_desc`, `create_time`, `update_time`)
VALUES ('admin', '系统管理员：拥有最高权限，负责系统配置和管理', NOW(), NOW()),
       ('user', '普通用户：可使用快递驿站的基本功能', NOW(), NOW()),
       ('station_admin', '驿站管理员：负责驿站的日常管理和运营', NOW(), NOW()),
       ('station_staff', '驿站普通工作人员：协助处理快递出入库等日常事务', NOW(), NOW());

-- 创建用户角色表
-- DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`
(
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `role_id`     BIGINT   NOT NULL COMMENT '角色ID',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_role_id` (`role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户与角色的关联关系表，支持一个用户拥有多个角色';

-- 创建权限表
-- DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`
(
    `permission_id`   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '权限唯一标识ID',
    `permission_code` VARCHAR(100) NOT NULL COMMENT '权限代码：如user:create, parcel:query',
    `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    `permission_desc` VARCHAR(255) NULL COMMENT '权限描述',
    `create_time`     DATETIME     NOT NULL COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL COMMENT '最后更新时间',
    PRIMARY KEY (`permission_id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='定义系统中具体的操作权限点';

-- 创建角色权限表
-- DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`
(
    `id`            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id`       BIGINT   NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT   NOT NULL COMMENT '权限ID',
    `create_time`   DATETIME NOT NULL COMMENT '关联创建时间',
    `update_time`   DATETIME NOT NULL COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    INDEX `idx_role_id` (`role_id`),
    INDEX `idx_permission_id` (`permission_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='角色与权限的关联关系表，实现灵活的权限控制';

-- Seata AT模式使用的表
-- DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT,
    `branch_id`     bigint(20)   NOT NULL,
    `xid`           varchar(100) NOT NULL,
    `context`       varchar(128) NOT NULL,
    `rollback_info` longblob     NOT NULL,
    `log_status`    int(11)      NOT NULL,
    `log_created`   datetime     NOT NULL,
    `log_modified`  datetime     NOT NULL,
    `ext`           varchar(100) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;