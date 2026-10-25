package com.mall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 商品服务启动类。
 *
 * <p>mall-common 的通用能力（traceId 过滤器、全局异常处理）通过自动装配生效，
 * 参见 {@code com.mall.common.web.CommonWebAutoConfiguration}。
 */
@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
