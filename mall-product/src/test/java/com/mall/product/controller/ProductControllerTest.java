package com.mall.product.controller;

import com.mall.product.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 商品接口集成测试。
 *
 * <p>依赖 Flyway 的 V1（建表）+ V2（种子数据），因此用例里的总数、SKU 数量
 * 与种子数据是一一对应的；修改种子数据时同步更新这里。
 */
class ProductControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("默认分页：返回 10 条，总数 10")
    void pageShouldReturnDefaultPage() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(10))
                .andExpect(jsonPath("$.data.pageNum").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.records.length()").value(10))
                // 默认按销量降序，销量最高的 1010（8800）排第一
                .andExpect(jsonPath("$.data.records[0].id").value(1010));
    }

    @Test
    @DisplayName("分页参数生效：pageSize=3 时总数 10、总页数 4")
    void pageShouldRespectPageSize() throws Exception {
        mockMvc.perform(get("/api/products").param("pageNum", "1").param("pageSize", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records.length()").value(3))
                .andExpect(jsonPath("$.data.total").value(10))
                .andExpect(jsonPath("$.data.pages").value(4));
    }

    @Test
    @DisplayName("按类目筛选：手机类目（11）下 2 个商品")
    void pageShouldFilterByCategory() throws Exception {
        mockMvc.perform(get("/api/products").param("categoryId", "11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(2));
    }

    @Test
    @DisplayName("关键字搜索：命中商品名称")
    void pageShouldFilterByKeyword() throws Exception {
        mockMvc.perform(get("/api/products").param("keyword", "iPhone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(1001));
    }

    @Test
    @DisplayName("商品详情：返回 2 个 SKU 与对应库存")
    void detailShouldReturnSkusWithStock() throws Exception {
        mockMvc.perform(get("/api/products/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1001))
                .andExpect(jsonPath("$.data.name").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.data.categoryId").value(11))
                .andExpect(jsonPath("$.data.skus.length()").value(2))
                // 规格 JSON 被解析成数组，每个 SKU 有 2 个规格项
                .andExpect(jsonPath("$.data.skus[0].specs.length()").value(2))
                // SKU 2001 的种子库存为 500
                .andExpect(jsonPath("$.data.skus[0].id").value(2001))
                .andExpect(jsonPath("$.data.skus[0].availableStock").value(500));
    }

    @Test
    @DisplayName("商品不存在：HTTP 200 + 业务码 20001")
    void detailShouldReturnNotFoundCode() throws Exception {
        mockMvc.perform(get("/api/products/999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20001));
    }

    @Test
    @DisplayName("分页参数非法：HTTP 400 + 10000")
    void pageShouldRejectInvalidPageSize() throws Exception {
        mockMvc.perform(get("/api/products").param("pageSize", "1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10000));
    }
}
