package com.mall.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 商品列表查询条件（GET 参数绑定）。
 *
 * <p>所有字段都带默认值，前端不传也能得到合理结果；
 * 上限由 {@link Max} 与 MyBatis-Plus 的 maxLimit 双重限制，防止恶意大分页。
 */
@Data
public class ProductPageQuery {

    /** 页码，从 1 开始 */
    @Min(value = 1, message = "页码从 1 开始")
    private long pageNum = 1;

    /** 每页条数，默认 10，最大 100 */
    @Min(value = 1, message = "每页至少 1 条")
    @Max(value = 100, message = "每页最多 100 条")
    private long pageSize = 10;

    /** 类目 ID，为空表示全部类目 */
    private Long categoryId;

    /** 关键字，会匹配商品名称与副标题；为空表示不过滤 */
    private String keyword;
}
