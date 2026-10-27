package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品类目。
 *
 * <p>采用邻接表（parent_id）存树，层级由 level 字段冗余标记。
 * 类目数量有限（通常几百个），后续可整体缓存到 Redis，无需复杂设计。
 */
@Data
@TableName("category")
public class Category {

    /** 类目 ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 父类目 ID，0 表示一级类目 */
    private Long parentId;

    /** 类目名称 */
    private String name;

    /** 层级：1-一级 2-二级 */
    private Integer level;

    /** 排序值，越小越靠前 */
    private Integer sort;

    /** 类目图标 URL */
    private String icon;

    /** 状态：1-启用 0-禁用 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
