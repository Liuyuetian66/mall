package com.mall.product.vo;

import java.math.BigDecimal;

/**
 * 商品列表项（列表页只返回必要字段，不含详情富文本）。
 *
 * @param id        商品 ID
 * @param name      商品名称
 * @param subtitle  副标题/卖点
 * @param mainImage 主图 URL
 * @param priceMin  最低售价，前端展示「¥xxx 起」
 * @param priceMax  最高售价
 * @param sales     累计销量
 */
public record ProductListVO(
        Long id,
        String name,
        String subtitle,
        String mainImage,
        BigDecimal priceMin,
        BigDecimal priceMax,
        Integer sales
) {
}
