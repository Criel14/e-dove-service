-- ================================================
-- 创建数据库与用户
-- ================================================

-- （1）创建数据库，如果不存在就创建
CREATE DATABASE IF NOT EXISTS `e_dove_parcel`
    -- 设置默认字符集为 utf8mb4
    DEFAULT CHARACTER SET utf8mb4
    -- 设置默认校对规则为 utf8mb4_unicode_ci
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- （2）删除已存在的用户（如果存在）
DROP USER IF EXISTS 'e_dove_parcel_admin'@'%';

-- （3）创建专门的用户，用于该数据库管理
CREATE USER IF NOT EXISTS 'e_dove_parcel_admin'@'%'
    IDENTIFIED BY 'eDoveMysql1014';

-- （4）授予该用户对数据库 e_dove_parcel 的所有权限
GRANT ALL PRIVILEGES
    ON `e_dove_parcel`.* -- 数据库名 + 所有表
    TO 'e_dove_parcel_admin'@'%';

-- （5）使权限变更立即生效
FLUSH PRIVILEGES;


-- ================================================
-- 创建表 并 初始化表数据
-- ================================================
USE e_dove_parcel;

-- 包裹表（parcel）
DROP TABLE IF EXISTS parcel;
CREATE TABLE parcel
(
    id                      BIGINT       NOT NULL COMMENT '雪花算法生成的包裹唯一 ID',
    tracking_number         VARCHAR(100) NOT NULL COMMENT '快递运单号',
    recipient_phone         VARCHAR(20)  NOT NULL COMMENT '收件人手机号',
    recipient_addr_province VARCHAR(50) COMMENT '收件人地址——省',
    recipient_addr_city     VARCHAR(50) COMMENT '收件人地址——市',
    recipient_addr_district VARCHAR(50) COMMENT '收件人地址——区／县',
    recipient_addr_detail   VARCHAR(200) COMMENT '收件人地址——详细地址',
    width                   DECIMAL(10, 2) COMMENT '包裹宽度（单位：cm）',
    height                  DECIMAL(10, 2) COMMENT '包裹高度（单位：cm）',
    length                  DECIMAL(10, 2) COMMENT '包裹长度（单位：cm）',
    weight                  DECIMAL(10, 3) COMMENT '包裹重量（单位：kg）',

    store_id                BIGINT       NOT NULL COMMENT '送至门店 ID',
    pick_code               VARCHAR(20) COMMENT '3段式取件码（已入库后生成，包含货架／层信息）',
    status                  INT DEFAULT 0 COMMENT '包裹状态（整型）：0=新包裹、1=已入库、2=已取出、3=滞留、4=退回',
    in_time                 DATETIME COMMENT '包裹入库时间',
    out_time                DATETIME COMMENT '包裹取件／出库时间',
    out_machine_id          BIGINT COMMENT '出库机器 ID',
    create_time             DATETIME     NOT NULL COMMENT '记录创建时间',
    update_time             DATETIME     NOT NULL COMMENT '记录最后更新时间',

    -- latest_logistics_status INT COMMENT '预留：最新物流状态（整型）',
    -- logistics_update_time   DATETIME COMMENT '预留：物流状态更新时间',
    -- logistics_provider_id   BIGINT COMMENT '预留：物流公司 ID',

    PRIMARY KEY (id),
    UNIQUE KEY uk_tracking_number_phone (tracking_number, recipient_phone), -- 唯一索引：运单号 + 手机号，方便出库时快速查找
    INDEX idx_recipient_phone (recipient_phone), -- 用户查询自己的包裹
    INDEX idx_tracking_number (tracking_number), -- 运单号查包裹
    INDEX idx_store_id (store_id), -- 门店查送至门店的包裹
    INDEX idx_status_in_time_id (status, in_time, id) -- 方便查找出滞留包裹
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '包裹信息表';


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