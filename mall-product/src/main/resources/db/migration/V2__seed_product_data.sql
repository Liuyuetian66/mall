-- ============================================================
-- V2：商品域种子数据（仅用于开发/演示）
-- ------------------------------------------------------------
-- 数据规模：7 个类目、10 个商品（SPU）、20 个 SKU、20 条库存
-- 说明：
--   1. 主键使用固定值，方便联调与文档引用，不要用在生产环境
--   2. price_min / price_max 与旗下 SKU 价格保持一致
--   3. 每个商品配 2 个 SKU，覆盖"颜色/容量/版本"等常见规格
-- ============================================================

-- ---------------------------- 类目 ----------------------------
INSERT INTO `category` (`id`, `parent_id`, `name`, `level`, `sort`, `status`)
VALUES (1, 0, '手机数码', 1, 1, 1),
       (2, 0, '电脑办公', 1, 2, 1),
       (3, 0, '家用电器', 1, 3, 1),
       (4, 0, '服饰鞋包', 1, 4, 1),
       (5, 0, '食品生鲜', 1, 5, 1),
       (11, 1, '手机', 2, 1, 1),
       (12, 1, '耳机音频', 2, 2, 1),
       (21, 2, '笔记本', 2, 1, 1);

-- ---------------------------- 商品 ----------------------------
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `main_image`, `detail`,
                       `price_min`, `price_max`, `sales`, `status`)
VALUES (1001, 11, 'iPhone 15 Pro', '钛金属设计，A17 Pro 芯片',
        'https://cdn.mall.example.com/p/1001-main.jpg', '<p>iPhone 15 Pro 商品详情</p>', 7999.00, 8999.00, 1200, 1),
       (1002, 11, '小米 14', '徕卡光学镜头，骁龙 8 Gen3',
        'https://cdn.mall.example.com/p/1002-main.jpg', '<p>小米 14 商品详情</p>', 3999.00, 4599.00, 3400, 1),
       (1003, 12, 'AirPods Pro 2', '主动降噪，自适应通透模式',
        'https://cdn.mall.example.com/p/1003-main.jpg', '<p>AirPods Pro 2 商品详情</p>', 1899.00, 1899.00, 2100, 1),
       (1004, 12, '索尼 WH-1000XM5', '头戴式无线降噪耳机',
        'https://cdn.mall.example.com/p/1004-main.jpg', '<p>索尼降噪耳机商品详情</p>', 2699.00, 2699.00, 860, 1),
       (1005, 21, 'MacBook Pro 14', 'M3 芯片，Liquid 视网膜 XDR 屏',
        'https://cdn.mall.example.com/p/1005-main.jpg', '<p>MacBook Pro 14 商品详情</p>', 14999.00, 18999.00, 320, 1),
       (1006, 21, 'ThinkPad X1 Carbon', '商务轻薄本，14 英寸 2.8K 屏',
        'https://cdn.mall.example.com/p/1006-main.jpg', '<p>ThinkPad X1 商品详情</p>', 9999.00, 12999.00, 210, 1),
       (1007, 3, '戴森 V15 Detect', '激光探测，智能感知灰尘',
        'https://cdn.mall.example.com/p/1007-main.jpg', '<p>戴森吸尘器商品详情</p>', 4299.00, 4299.00, 150, 1),
       (1008, 3, '美的 电饭煲 MB-RE4023', '4L 容量，IH 电磁加热',
        'https://cdn.mall.example.com/p/1008-main.jpg', '<p>美的电饭煲商品详情</p>', 399.00, 599.00, 5200, 1),
       (1009, 4, 'Nike Air Zoom Pegasus 41', '全掌气垫，日常跑步训练',
        'https://cdn.mall.example.com/p/1009-main.jpg', '<p>Nike 跑鞋商品详情</p>', 699.00, 899.00, 3300, 1),
       (1010, 5, '三只松鼠 坚果礼盒', '每日坚果，混合口味 1.5kg',
        'https://cdn.mall.example.com/p/1010-main.jpg', '<p>坚果礼盒商品详情</p>', 129.00, 199.00, 8800, 1);

-- ---------------------------- SKU ----------------------------
INSERT INTO `sku` (`id`, `product_id`, `sku_code`, `spec_json`, `price`, `image`, `status`)
VALUES (2001, 1001, 'IP15P-128-BLK', '[{"name":"容量","value":"128GB"},{"name":"颜色","value":"黑色"}]', 7999.00,
        'https://cdn.mall.example.com/s/2001.jpg', 1),
       (2002, 1001, 'IP15P-256-NAT', '[{"name":"容量","value":"256GB"},{"name":"颜色","value":"原色钛金属"}]', 8999.00,
        'https://cdn.mall.example.com/s/2002.jpg', 1),
       (2003, 1002, 'MI14-256-BLK', '[{"name":"内存","value":"12GB"},{"name":"存储","value":"256GB"},{"name":"颜色","value":"黑色"}]',
        3999.00, 'https://cdn.mall.example.com/s/2003.jpg', 1),
       (2004, 1002, 'MI14-512-WHT', '[{"name":"内存","value":"16GB"},{"name":"存储","value":"512GB"},{"name":"颜色","value":"白色"}]',
        4599.00, 'https://cdn.mall.example.com/s/2004.jpg', 1),
       (2005, 1003, 'APP2-STD', '[{"name":"版本","value":"标准版"}]', 1899.00,
        'https://cdn.mall.example.com/s/2005.jpg', 1),
       (2006, 1003, 'APP2-USBC', '[{"name":"版本","value":"USB-C 版"}]', 1899.00,
        'https://cdn.mall.example.com/s/2006.jpg', 1),
       (2007, 1004, 'XM5-BLK', '[{"name":"颜色","value":"黑色"}]', 2699.00,
        'https://cdn.mall.example.com/s/2007.jpg', 1),
       (2008, 1004, 'XM5-PLT', '[{"name":"颜色","value":"铂金银"}]', 2699.00,
        'https://cdn.mall.example.com/s/2008.jpg', 1),
       (2009, 1005, 'MBP14-M3-16-512', '[{"name":"芯片","value":"M3"},{"name":"内存","value":"16GB"},{"name":"存储","value":"512GB"}]',
        14999.00, 'https://cdn.mall.example.com/s/2009.jpg', 1),
       (2010, 1005, 'MBP14-M3P-18-1T', '[{"name":"芯片","value":"M3 Pro"},{"name":"内存","value":"18GB"},{"name":"存储","value":"1TB"}]',
        18999.00, 'https://cdn.mall.example.com/s/2010.jpg', 1),
       (2011, 1006, 'X1C-I7-16-512', '[{"name":"配置","value":"i7/16GB/512GB"}]', 9999.00,
        'https://cdn.mall.example.com/s/2011.jpg', 1),
       (2012, 1006, 'X1C-I7-32-1T', '[{"name":"配置","value":"i7/32GB/1TB"}]', 12999.00,
        'https://cdn.mall.example.com/s/2012.jpg', 1),
       (2013, 1007, 'V15-DETECT', '[{"name":"型号","value":"V15 Detect"}]', 4299.00,
        'https://cdn.mall.example.com/s/2013.jpg', 1),
       (2014, 1007, 'V15-ABS', '[{"name":"型号","value":"V15 Detect Absolute"}]', 4299.00,
        'https://cdn.mall.example.com/s/2014.jpg', 1),
       (2015, 1008, 'MB-RE4023-4L', '[{"name":"容量","value":"4L"}]', 399.00,
        'https://cdn.mall.example.com/s/2015.jpg', 1),
       (2016, 1008, 'MB-RE4023-5L', '[{"name":"容量","value":"5L"}]', 599.00,
        'https://cdn.mall.example.com/s/2016.jpg', 1),
       (2017, 1009, 'NZP41-BLK', '[{"name":"颜色","value":"黑色"},{"name":"尺码","value":"42"}]', 699.00,
        'https://cdn.mall.example.com/s/2017.jpg', 1),
       (2018, 1009, 'NZP41-WHT', '[{"name":"颜色","value":"白色"},{"name":"尺码","value":"43"}]', 899.00,
        'https://cdn.mall.example.com/s/2018.jpg', 1),
       (2019, 1010, 'SSS-CLASSIC', '[{"name":"规格","value":"经典款 1.5kg"}]', 129.00,
        'https://cdn.mall.example.com/s/2019.jpg', 1),
       (2020, 1010, 'SSS-PREMIUM', '[{"name":"规格","value":"尊享款 1.5kg"}]', 199.00,
        'https://cdn.mall.example.com/s/2020.jpg', 1);

-- ---------------------------- 库存 ----------------------------
INSERT INTO `inventory` (`sku_id`, `total_stock`, `available_stock`, `locked_stock`, `version`)
VALUES (2001, 500, 500, 0, 0),
       (2002, 300, 300, 0, 0),
       (2003, 800, 800, 0, 0),
       (2004, 600, 600, 0, 0),
       (2005, 1000, 1000, 0, 0),
       (2006, 1000, 1000, 0, 0),
       (2007, 400, 400, 0, 0),
       (2008, 350, 350, 0, 0),
       (2009, 120, 120, 0, 0),
       (2010, 80, 80, 0, 0),
       (2011, 200, 200, 0, 0),
       (2012, 150, 150, 0, 0),
       (2013, 100, 100, 0, 0),
       (2014, 60, 60, 0, 0),
       (2015, 2000, 2000, 0, 0),
       (2016, 1500, 1500, 0, 0),
       (2017, 900, 900, 0, 0),
       (2018, 700, 700, 0, 0),
       (2019, 5000, 5000, 0, 0),
       (2020, 3000, 3000, 0, 0);
