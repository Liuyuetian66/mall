package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品（SPU，Standard Product Unit）。
 *
 * <p>只存"是什么"的共性信息；具体可售单元（颜色/容量）在 {@link Sku} 中。
 *
 * <p>注意：
 * <ul>
 *     <li>{@code detail} 是 TEXT 大字段，列表查询时不应查询它，
 *         因此 Service 层用 select 指定列 / 单独查询控制</li>
 *     <li>{@code priceMin} / {@code priceMax} 是 SKU 价格的冗余，
 *         列表页展示"¥7999 起"用，修改 SKU 价格时必须同步维护</li>
 * </ul>
 */
@Data
@TableName("product")
public class Product {

    /** 商品 ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属类目 ID */
    private Long categoryId;

    /** 商品名称 */
    private String name;

    /** 副标题/卖点 */
    private String subtitle;

    /** 主图 URL */
    private String mainImage;

    /** 详情富文本，大字段，仅详情页查询 */
    private String detail;

    /** 最低售价 */
    private BigDecimal priceMin;

    /** 最高售价 */
    private BigDecimal priceMax;

    /** 累计销量，用于列表排序 */
    private Integer sales;

    /** 状态：1-上架 0-下架 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
