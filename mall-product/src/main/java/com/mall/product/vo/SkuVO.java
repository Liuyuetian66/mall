package com.mall.product.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * SKU 视图对象（详情页的可选项）。
 *
 * @param id             SKU ID，下单时传给后端的是这个 ID
 * @param skuCode        SKU 编码
 * @param specs          规格列表，从 spec_json 解析而来；解析失败时为空列表
 * @param price          售价
 * @param image          SKU 图片
 * @param availableStock 可用库存，前端用于置灰「已售罄」
 */
public record SkuVO(
        Long id,
        String skuCode,
        List<SpecItem> specs,
        BigDecimal price,
        String image,
        Integer availableStock
) {
}
