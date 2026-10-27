package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * SKU 库存。
 *
 * <p>为什么库存独立于 SKU：
 * <ul>
 *     <li>库存是高频写、强一致要求的字段，与商品信息（低频写）拆开后，
 *         可以单独做主从/分片/热点优化</li>
 *     <li>后续扣减会引入 Redis 预扣 + 数据库乐观锁兜底，库存表需要 version 字段</li>
 * </ul>
 *
 * <p>数量关系：{@code availableStock = totalStock - lockedStock}。
 * 表上有 CHECK 约束保证两个数量非负，防止代码 bug 写出脏数据。
 */
@Data
@TableName("inventory")
public class Inventory {

    /** SKU ID，与 sku 表一对一，主键由业务指定（非自增） */
    @TableId(value = "sku_id", type = IdType.INPUT)
    private Long skuId;

    /** 总库存 */
    private Integer totalStock;

    /** 可用库存 */
    private Integer availableStock;

    /** 锁定库存（下单未支付占用） */
    private Integer lockedStock;

    /** 乐观锁版本号，Day 2 仅建字段，下单扣减时启用 */
    private Integer version;

    private LocalDateTime updateTime;
}
