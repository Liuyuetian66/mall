package com.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.common.api.PageResult;
import com.mall.common.api.ResultCode;
import com.mall.common.exception.BizException;
import com.mall.product.dto.ProductPageQuery;
import com.mall.product.entity.Category;
import com.mall.product.entity.Inventory;
import com.mall.product.entity.Product;
import com.mall.product.entity.Sku;
import com.mall.product.mapper.CategoryMapper;
import com.mall.product.mapper.InventoryMapper;
import com.mall.product.mapper.ProductMapper;
import com.mall.product.mapper.SkuMapper;
import com.mall.product.service.ProductService;
import com.mall.product.vo.ProductDetailVO;
import com.mall.product.vo.ProductListVO;
import com.mall.product.vo.SkuVO;
import com.mall.product.vo.SpecItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商品查询服务实现。
 *
 * <p>读链路的三个约定：
 * <ol>
 *     <li>列表查询**不查 detail 大字段**，只 select 必要列，减少 IO 与网络传输</li>
 *     <li>详情页的 SKU 与库存各一次批量查询，避免 N+1</li>
 *     <li>库存缺失时返回 0 而不是报错，保证商品仍可展示（前端置灰）</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    /** 上架 / 可售状态 */
    private static final int STATUS_ON = 1;

    /** 规格 JSON 的泛型类型，避免每次调用重复构造 */
    private static final TypeReference<List<SpecItem>> SPEC_TYPE = new TypeReference<>() {
    };

    private final ProductMapper productMapper;
    private final SkuMapper skuMapper;
    private final CategoryMapper categoryMapper;
    private final InventoryMapper inventoryMapper;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<ProductListVO> page(ProductPageQuery query) {
        Page<Product> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Product> wrapper = Wrappers.<Product>lambdaQuery()
                // 只查列表需要的列，排除 detail / create_time 等无用字段
                .select(Product::getId, Product::getName, Product::getSubtitle, Product::getMainImage,
                        Product::getPriceMin, Product::getPriceMax, Product::getSales)
                .eq(Product::getStatus, STATUS_ON)
                // 条件生效与否由第一个 boolean 参数控制，避免拼 SQL 字符串
                .eq(query.getCategoryId() != null, Product::getCategoryId, query.getCategoryId())
                .and(StringUtils.hasText(query.getKeyword()),
                        w -> w.like(Product::getName, query.getKeyword())
                                .or().like(Product::getSubtitle, query.getKeyword()))
                .orderByDesc(Product::getSales)
                .orderByDesc(Product::getId);

        Page<Product> result = productMapper.selectPage(page, wrapper);

        List<ProductListVO> records = result.getRecords().stream()
                .map(this::toListVO)
                .toList();
        return PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public ProductDetailVO detail(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BizException(ResultCode.PRODUCT_NOT_FOUND);
        }
        if (!Objects.equals(product.getStatus(), STATUS_ON)) {
            throw new BizException(ResultCode.PRODUCT_OFF_SHELF);
        }

        Category category = product.getCategoryId() == null
                ? null
                : categoryMapper.selectById(product.getCategoryId());

        // 只返回可售 SKU，按价格升序，前端默认选中第一个
        List<Sku> skus = skuMapper.selectList(Wrappers.<Sku>lambdaQuery()
                .eq(Sku::getProductId, productId)
                .eq(Sku::getStatus, STATUS_ON)
                .orderByAsc(Sku::getPrice));

        Map<Long, Integer> stockMap = loadStock(skus);
        List<SkuVO> skuViews = skus.stream()
                .map(sku -> toSkuVO(sku, stockMap.getOrDefault(sku.getId(), 0)))
                .toList();

        return new ProductDetailVO(
                product.getId(),
                product.getName(),
                product.getSubtitle(),
                product.getMainImage(),
                product.getDetail(),
                product.getCategoryId(),
                category == null ? null : category.getName(),
                product.getPriceMin(),
                product.getPriceMax(),
                product.getSales(),
                skuViews);
    }

    /**
     * 批量查询库存，一次 SQL 解决，避免按 SKU 循环查询造成 N+1。
     */
    private Map<Long, Integer> loadStock(List<Sku> skus) {
        if (skus.isEmpty()) {
            return Map.of();
        }
        List<Long> skuIds = skus.stream().map(Sku::getId).toList();
        return inventoryMapper.selectList(Wrappers.<Inventory>lambdaQuery()
                        .in(Inventory::getSkuId, skuIds))
                .stream()
                .collect(Collectors.toMap(Inventory::getSkuId, Inventory::getAvailableStock));
    }

    private ProductListVO toListVO(Product product) {
        return new ProductListVO(
                product.getId(),
                product.getName(),
                product.getSubtitle(),
                product.getMainImage(),
                product.getPriceMin(),
                product.getPriceMax(),
                product.getSales());
    }

    private SkuVO toSkuVO(Sku sku, int availableStock) {
        return new SkuVO(
                sku.getId(),
                sku.getSkuCode(),
                parseSpec(sku.getSpecJson()),
                sku.getPrice(),
                sku.getImage(),
                availableStock);
    }

    /**
     * 解析规格 JSON。
     *
     * <p>规格只用于展示，解析失败时降级为空列表并记录 WARN，
     * 不让一条脏数据导致整个商品详情接口 500。
     */
    private List<SpecItem> parseSpec(String specJson) {
        if (!StringUtils.hasText(specJson)) {
            return List.of();
        }
        try {
            return objectMapper.readValue(specJson, SPEC_TYPE);
        } catch (Exception e) {
            log.warn("SKU 规格解析失败 | specJson={}", specJson, e);
            return List.of();
        }
    }
}
