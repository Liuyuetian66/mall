package com.mall.product;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 集成测试基类。
 *
 * <p>测试连接「独立的测试库」{@code mall_test}（见 {@code src/test/resources/application-test.yml}），
 * 与开发库 {@code mall} 隔离：
 * <ul>
 *     <li>Flyway 在测试上下文启动时自动建表并灌入种子数据</li>
 *     <li>跑测试不会影响你本地开发时手工造的数据</li>
 * </ul>
 *
 * <p>前置条件：本地 Docker 里的 MySQL 已启动（{@code docker compose up -d}）。
 * 之所以不用 Testcontainers：当前环境 Docker Engine 29 的 API 与 Testcontainers
 * 内置的 docker-java 客户端不兼容（/info 直接返回 400），无法建立连接；
 * 待其支持后可以把本类切回容器方案，测试代码无需改动。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {
}
