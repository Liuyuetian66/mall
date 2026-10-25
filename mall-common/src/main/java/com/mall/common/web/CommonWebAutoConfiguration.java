package com.mall.common.web;

import com.mall.common.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 * mall-common 的 Web 自动装配。
 *
 * <p>mall-common 不在业务模块的包扫描路径下（业务模块扫描自己的 com.mall.product 等），
 * 因此通过 Spring Boot 的自动装配机制把通用能力注入进去，
 * 业务模块无需任何额外配置，只要引入依赖即可生效。
 *
 * <p>注册入口见：
 * {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CommonWebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TraceIdFilter traceIdFilter() {
        return new TraceIdFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
