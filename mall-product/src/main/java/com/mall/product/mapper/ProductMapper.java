package com.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.product.entity.Product;

/**
 * 商品 Mapper。
 *
 * <p>Day 2 的所有查询都能用条件构造器完成，暂无自定义 SQL；
 * 后续出现多表 join / 复杂统计时，再在 resources/mapper 下加 XML。
 */
public interface ProductMapper extends BaseMapper<Product> {
}
