package com.mall.common.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * ID 生成工具。当前仅用于 traceId，后续阶段会补充雪花算法 / 号段模式（M5）。
 */
public final class IdUtils {

    private IdUtils() {
    }

    /**
     * 生成 traceId：32 位十六进制，前缀为时间戳，便于按时间排序和快速定位。
     * <p>示例：{@code 18f3a1b2c3d_9a1f2b3c4d5e6f70}
     */
    public static String newTraceId() {
        String timePart = Long.toHexString(System.currentTimeMillis());
        String randomPart = Long.toHexString(ThreadLocalRandom.current().nextLong());
        return (timePart + "_" + randomPart).replace("-", "");
    }

    /** 简易 UUID，用于日志关联等非严格唯一场景 */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
