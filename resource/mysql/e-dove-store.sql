-- ================================================
-- 创建数据库与用户
-- ================================================

-- （1）创建数据库，如果不存在就创建
CREATE DATABASE IF NOT EXISTS `e_dove_store`
    -- 设置默认字符集为 utf8mb4
    DEFAULT CHARACTER SET utf8mb4
    -- 设置默认校对规则为 utf8mb4_unicode_ci
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- （2）删除已存在的用户（如果存在）
DROP USER IF EXISTS 'e_dove_store_admin'@'%';

-- （3）创建专门的用户，用于该数据库管理
CREATE USER IF NOT EXISTS 'e_dove_store_admin'@'%'
    IDENTIFIED BY 'eDoveMysql1014';

-- （4）授予该用户对数据库 e_dove_store 的所有权限
GRANT ALL PRIVILEGES
    ON `e_dove_store`.* -- 数据库名 + 所有表
    TO 'e_dove_store_admin'@'%';

-- （5）使权限变更立即生效
FLUSH PRIVILEGES;


-- ================================================
-- 创建表 并 初始化表数据
-- ================================================
USE e_dove_store;

-- 门店表（store）
CREATE TABLE store
(
    id              BIGINT       NOT NULL COMMENT '雪花算法生成的门店唯一 ID',
    manager_user_id BIGINT COMMENT '店长用户 ID',
    manager_phone   VARCHAR(20) COMMENT '店长手机号',
    store_name      VARCHAR(200) NOT NULL COMMENT '门店名称',
    addr_province   VARCHAR(50) COMMENT '门店地址——省',
    addr_city       VARCHAR(50) COMMENT '门店地址——市',
    addr_district   VARCHAR(50) COMMENT '门店地址——区／县',
    addr_detail     VARCHAR(200) COMMENT '门店详细地址',
    status          INT DEFAULT 1 COMMENT '门店状态（整型）：如 1=营业、2=休息、3=注销',
    -- latitude        DECIMAL(10, 6) COMMENT '地理位置：纬度',
    -- longitude       DECIMAL(10, 6) COMMENT '地理位置：经度',
    create_time     DATETIME     NOT NULL COMMENT '记录创建时间',
    update_time     DATETIME     NOT NULL COMMENT '记录最后更新时间',

    PRIMARY KEY (id),
    INDEX idx_manager_user (manager_user_id),
    INDEX idx_status (status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '门店信息表';

-- 货架表（shelf）
CREATE TABLE shelf
(
    id          BIGINT   NOT NULL COMMENT '雪花算法生成的货架唯一 ID',
    store_id    BIGINT   NOT NULL COMMENT '所属门店 ID',
    shelf_no    INT      NOT NULL COMMENT '门店内部货架编号（整数）',
    layer_count INT      NOT NULL COMMENT '货架总层数',
    max_width   DECIMAL(10, 2) COMMENT '货架可放包裹最大宽度（cm）',
    max_height  DECIMAL(10, 2) COMMENT '货架可放包裹最大高度（cm）',
    max_length  DECIMAL(10, 2) COMMENT '货架可放包裹最大长度（cm）',
    max_weight  DECIMAL(10, 3) COMMENT '货架可承受最大重量（kg）',
    status      INT DEFAULT 1 COMMENT '货架状态（整型）：1=正常、0=停用或维修等',
    create_time DATETIME NOT NULL COMMENT '记录创建时间',
    update_time DATETIME NOT NULL COMMENT '记录最后更新时间',

    PRIMARY KEY (id),
    UNIQUE KEY uk_store_shelf (store_id, shelf_no), -- 唯一索引：门店ID + 货架编号
    INDEX idx_store_id (store_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '货架表';

-- 货架层表（shelf_layer）
CREATE TABLE shelf_layer
(
    id            BIGINT   NOT NULL COMMENT '雪花算法生成的层唯一 ID',
    shelf_id      BIGINT   NOT NULL COMMENT '所属货架 ID',
    layer_no      INT      NOT NULL COMMENT '层号编号（从 1 开始）',
    today_max_seq INT DEFAULT 0 COMMENT '当天最大序号，用于取件码序列（每日重置）',
    max_capacity  INT      NOT NULL COMMENT '最大编号上限 / 最多可存放包裹数量',
    create_time   DATETIME NOT NULL COMMENT '记录创建时间',
    update_time   DATETIME NOT NULL COMMENT '记录最后更新时间',

    PRIMARY KEY (id),
    INDEX idx_shelf_id (shelf_id),
    UNIQUE KEY uk_shelf_layer (shelf_id, layer_no) -- 唯一索引：货架ID + 层号
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '货架层表';


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