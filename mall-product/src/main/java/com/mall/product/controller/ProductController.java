package com.mall.product.controller;

import com.mall.common.api.PageResult;
import com.mall.common.api.Result;
import com.mall.product.dto.ProductPageQuery;
import com.mall.product.service.ProductService;
import com.mall.product.vo.ProductDetailVO;
import com.mall.product.vo.ProductListVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品查询接口。
 *
 * <p>Controller 只做三件事：接参数、调 Service、包 Result。
 * 不做业务判断、不写 SQL、不 try-catch（异常统一由 GlobalExceptionHandler 处理）。
 */
@Validated
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 商品列表（分页）。
     *
     * <p>示例：{@code GET /api/products?pageNum=1&pageSize=10&categoryId=11&keyword=iPhone}
     *
     * @param query 查询条件，pageSize 最大 100
     */
    @GetMapping
    public Result<PageResult<ProductListVO>> page(@Valid ProductPageQuery query) {
        return Result.ok(productService.page(query));
    }

    /**
     * 商品详情（含 SKU 与库存）。
     *
     * <p>示例：{@code GET /api/products/1001}
     *
     * @param id 商品 ID
     */
    @GetMapping("/{id}")
    public Result<ProductDetailVO> detail(@PathVariable("id") Long id) {
        return Result.ok(productService.detail(id));
    }
}
