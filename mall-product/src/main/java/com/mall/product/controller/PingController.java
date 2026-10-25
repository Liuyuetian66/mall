package com.mall.product.controller;

import com.mall.common.api.Result;
import com.mall.common.api.ResultCode;
import com.mall.common.exception.BizException;
import com.mall.product.vo.PingVO;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

/**
 * Day 1 自检接口：用于验证工程骨架、统一响应、异常处理、traceId 是否全部生效。
 *
 * <p>正式开发商品接口后可以删除本控制器。
 */
@Validated
@RestController
@RequestMapping("/ping")
public class PingController {

    @Value("${spring.profiles.active:default}")
    private String profile;

    /** 基础连通性检查 */
    @GetMapping
    public Result<PingVO> ping() {
        return Result.ok(new PingVO(
                "mall-product",
                "UP",
                OffsetDateTime.now().toString(),
                System.getProperty("java.version"),
                profile
        ));
    }

    /** 验证业务异常 → HTTP 200 + 业务错误码 */
    @GetMapping("/biz-error")
    public Result<Void> bizError() {
        throw new BizException(ResultCode.STOCK_NOT_ENOUGH, "商品库存不足，当前可售 0 件");
    }

    /** 验证参数校验 → HTTP 400 + 10000 */
    @GetMapping("/validate")
    public Result<String> validate(@RequestParam @NotBlank(message = "不能为空") String name) {
        return Result.ok(name);
    }

    /** 验证未知异常 → HTTP 500 + 99999 */
    @GetMapping("/boom")
    public Result<Void> boom() {
        throw new IllegalStateException("演示用：模拟未捕获异常");
    }
}
