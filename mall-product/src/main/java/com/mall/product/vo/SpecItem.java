package com.mall.product.vo;

/**
 * 规格项，对应数据库 sku.spec_json 中的一个元素。
 *
 * @param name  规格名，如「颜色」
 * @param value 规格值，如「黑色」
 */
public record SpecItem(String name, String value) {
}
