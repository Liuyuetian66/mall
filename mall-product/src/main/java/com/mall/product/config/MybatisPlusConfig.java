package com.mall.product.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置。
 *
 * <p>两个拦截器：
 * <ul>
 *     <li>{@link PaginationInnerInterceptor}：物理分页。不配它的话
 *         {@code selectPage} 会退化成内存分页（查询全表再截取），是大忌</li>
 *     <li>{@link BlockAttackInnerInterceptor}：拦截没有 where 条件的
 *         update/delete，防止误操作全表</li>
 * </ul>
 */
@Configuration
@MapperScan("com.mall.product.mapper")
public class MybatisPlusConfig {

    /** 单页最大条数，防止外部传入 pageSize=100000 把数据库打挂 */
    private static final long MAX_PAGE_SIZE = 100L;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        pagination.setMaxLimit(MAX_PAGE_SIZE);
        // 页码越界时返回空集合，而不是回到第一页（避免前端无限翻页）
        pagination.setOverflow(false);
        interceptor.addInnerInterceptor(pagination);

        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }
}
