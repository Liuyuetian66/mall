package com.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.product.entity.Category;

/**
 * 类目 Mapper。
 *
 * <p>继承 {@link BaseMapper} 后即拥有单表 CRUD，无需写任何 XML。
 * 由 {@code com.mall.product.config.MybatisPlusConfig} 上的 @MapperScan 统一注册。
 */
public interface CategoryMapper extends BaseMapper<Category> {
}
