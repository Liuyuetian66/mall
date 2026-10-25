package com.mall.product.vo;

/**
 * 健康检查返回体。
 *
 * <p>使用 record，JDK 21 + Jackson 可直接序列化，无需额外注解。
 */
public record PingVO(
        String service,
        String status,
        String serverTime,
        String javaVersion,
        String profile
) {
}
