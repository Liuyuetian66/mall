-- ============================================================
-- V1：商品域基础表
-- ------------------------------------------------------------
-- 设计说明：
--   1. 商品采用 SPU + SKU 模型：product 存共性信息，sku 存可售单元
--   2. 库存独立成表（inventory），不放在 sku 上：
--      - 后续分库分表时库存是独立热点，便于单独扩缩容
--      - 扣减用乐观锁 version，避免行锁竞争
--   3. 所有主键 BIGINT，由应用侧雪花算法生成（MyBatis-Plus ASSIGN_ID）
--   4. 金额统一 DECIMAL(10,2)，禁止用 float/double
--   5. 字符集 utf8mb4，支持 emoji（商品标题常见）
-- ============================================================

-- ------------------------------------------------------------
-- 商品类目表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `category`
(
    `id`          BIGINT       NOT NULL COMMENT '类目ID',
    `parent_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '父类目ID，0 表示一级类目',
    `name`        VARCHAR(64)  NOT NULL COMMENT '类目名称',
    `level`       TINYINT      NOT NULL DEFAULT 1 COMMENT '层级：1-一级 2-二级',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
    `icon`        VARCHAR(512)          DEFAULT NULL COMMENT '类目图标 URL',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='商品类目表';

-- ------------------------------------------------------------
-- 商品表（SPU：标准化产品单元，代表"是什么"）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `product`
(
    `id`          BIGINT         NOT NULL COMMENT '商品ID（SPU）',
    `category_id` BIGINT         NOT NULL COMMENT '所属类目ID',
    `name`        VARCHAR(128)   NOT NULL COMMENT '商品名称',
    `subtitle`    VARCHAR(256)            DEFAULT NULL COMMENT '副标题/卖点',
    `main_image`  VARCHAR(512)            DEFAULT NULL COMMENT '主图 URL',
    `detail`      TEXT                    DEFAULT NULL COMMENT '详情富文本（大字段，详情页才查）',
    `price_min`   DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最低售价（冗余自 SKU，便于列表展示）',
    `price_max`   DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最高售价',
    `sales`       INT            NOT NULL DEFAULT 0 COMMENT '累计销量，用于排序',
    `status`      TINYINT        NOT NULL DEFAULT 1 COMMENT '状态：1-上架 0-下架',
    `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status_sales` (`status`, `sales` DESC),
    KEY `idx_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='商品表（SPU）';

-- ------------------------------------------------------------
-- 商品 SKU 表（库存量单位，代表"具体能买哪一款"）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sku`
(
    `id`          BIGINT         NOT NULL COMMENT 'SKU ID',
    `product_id`  BIGINT         NOT NULL COMMENT '所属商品ID',
    `sku_code`    VARCHAR(64)    NOT NULL COMMENT 'SKU 编码，业务唯一',
    `spec_json`   JSON                    DEFAULT NULL COMMENT '规格 JSON，如 [{"name":"颜色","value":"黑色"}]',
    `price`       DECIMAL(10, 2) NOT NULL COMMENT '售价',
    `image`       VARCHAR(512)            DEFAULT NULL COMMENT 'SKU 图片',
    `status`      TINYINT        NOT NULL DEFAULT 1 COMMENT '状态：1-可售 0-停售',
    `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sku_code` (`sku_code`),
    KEY `idx_product_id` (`product_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='商品 SKU 表';

-- ------------------------------------------------------------
-- 库存表（与 SKU 一对一，独立于商品信息）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventory`
(
    `sku_id`          BIGINT   NOT NULL COMMENT 'SKU ID',
    `total_stock`     INT      NOT NULL DEFAULT 0 COMMENT '总库存',
    `available_stock` INT      NOT NULL DEFAULT 0 COMMENT '可用库存 = 总库存 - 锁定库存',
    `locked_stock`    INT      NOT NULL DEFAULT 0 COMMENT '锁定库存（下单未支付占用）',
    `version`         INT      NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `update_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`sku_id`),
    CONSTRAINT `ck_inventory_stock` CHECK (`available_stock` >= 0 AND `locked_stock` >= 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='商品库存表';
