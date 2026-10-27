package com.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.product.entity.Inventory;

/**
 * 库存 Mapper。
 *
 * <p>后续下单扣减会在这里扩展自定义 SQL（乐观锁 update ... where available_stock >= ?），
 * Day 2 只做查询。
 */
public interface InventoryMapper extends BaseMapper<Inventory> {
}
