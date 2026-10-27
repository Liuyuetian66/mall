package com.mall.product.service;

import com.mall.common.api.PageResult;
import com.mall.product.dto.ProductPageQuery;
import com.mall.product.vo.ProductDetailVO;
import com.mall.product.vo.ProductListVO;

/**
 * 商品查询服务。
 *
 * <p>Day 2 只有读接口；写接口（上下架、改价、库存调整）留到后台管理模块。
 */
public interface ProductService {

    /**
     * 分页查询上架商品。
     *
     * @param query 分页与筛选条件
     * @return 分页结果，无数据时 records 为空集合而非 null
     */
    PageResult<ProductListVO> page(ProductPageQuery query);

    /**
     * 查询商品详情（含全部可售 SKU 与库存）。
     *
     * @param productId 商品 ID
     * @return 商品详情
     * @throws com.mall.common.exception.BizException 商品不存在（20001）或已下架（20002）
     */
    ProductDetailVO detail(Long productId);
}
