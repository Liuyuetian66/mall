package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品 SKU（Stock Keeping Unit），真正参与交易的最小单元。
 *
 * <p>{@code specJson} 存规格数组，例如：
 * <pre>[{"name":"容量","value":"128GB"},{"name":"颜色","value":"黑色"}]</pre>
 * 用 JSON 列而不是多张规格表，是为了在商品规格维度不固定时保持灵活；
 * 后续若需要按规格筛选，可再抽 spec / spec_value 维表或同步到 ES。
 */
@Data
@TableName("sku")
public class Sku {

    /** SKU ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属商品 ID */
    private Long productId;

    /** SKU 编码，业务唯一（唯一索引 uk_sku_code） */
    private String skuCode;

    /** 规格 JSON 字符串 */
    private String specJson;

    /** 售价 */
    private BigDecimal price;

    /** SKU 图片 */
    private String image;

    /** 状态：1-可售 0-停售 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
