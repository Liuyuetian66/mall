package com.mall.product.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品详情。
 *
 * <p>相比列表项多了：详情富文本、类目名称、SKU 列表（含库存）。
 *
 * @param id           商品 ID
 * @param name         商品名称
 * @param subtitle     副标题/卖点
 * @param mainImage    主图 URL
 * @param detail       详情富文本 HTML
 * @param categoryId   类目 ID
 * @param categoryName 类目名称
 * @param priceMin     最低售价
 * @param priceMax     最高售价
 * @param sales        累计销量
 * @param skus         可售 SKU 列表，按价格升序
 */
public record ProductDetailVO(
        Long id,
        String name,
        String subtitle,
        String mainImage,
        String detail,
        Long categoryId,
        String categoryName,
        BigDecimal priceMin,
        BigDecimal priceMax,
        Integer sales,
        List<SkuVO> skus
) {
}
