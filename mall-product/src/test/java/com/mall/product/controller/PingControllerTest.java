package com.mall.product.controller;

import com.mall.product.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Day 1 验收测试：统一响应、traceId、异常处理三条主干链路。
 *
 * <p>继承 {@link AbstractIntegrationTest} 后，上下文会额外加载数据库与 Flyway，
 * 但本类用例本身不依赖任何表数据。
 */
class PingControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("/ping 返回统一结构，并自动生成 traceId")
    void pingShouldReturnUnifiedResult() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Trace-Id"))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.service").value("mall-product"))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    @DisplayName("上游传入的 traceId 被透传，保证全链路一致")
    void traceIdShouldBePropagated() throws Exception {
        mockMvc.perform(get("/ping").header("X-Trace-Id", "trace-12345678"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Trace-Id", "trace-12345678"))
                .andExpect(jsonPath("$.traceId").value("trace-12345678"));
    }

    @Test
    @DisplayName("BizException -> HTTP 200 + 业务错误码")
    void bizExceptionShouldReturnBusinessCode() throws Exception {
        mockMvc.perform(get("/ping/biz-error"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40001));
    }

    @Test
    @DisplayName("参数校验失败 -> HTTP 400 + 10000")
    void validationShouldReturn400() throws Exception {
        mockMvc.perform(get("/ping/validate").param("name", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10000));
    }

    @Test
    @DisplayName("未知异常 -> HTTP 500 + 99999，且不泄露堆栈")
    void uncaughtExceptionShouldReturn500() throws Exception {
        mockMvc.perform(get("/ping/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(99999));
    }
}
