package com.mall.common.api;

import com.mall.common.web.TraceContext;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应结构。所有 HTTP 接口都必须返回该结构。
 *
 * <pre>
 * {
 *   "code": 0,
 *   "message": "成功",
 *   "data": {...},
 *   "traceId": "18f3a...",
 *   "timestamp": 1729871234567
 * }
 * </pre>
 *
 * @param <T> 业务数据类型
 */
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 业务状态码，0 表示成功 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 业务数据 */
    private T data;

    /** 链路追踪 ID，排查问题时交给后端即可 */
    private String traceId;

    /** 服务器时间戳（毫秒） */
    private long timestamp;

    public Result() {
        this.traceId = TraceContext.getTraceId();
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.code = ResultCode.SUCCESS.getCode();
        result.message = ResultCode.SUCCESS.getMessage();
        result.data = data;
        return result;
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMessage());
    }

    public static <T> Result<T> fail(ResultCode resultCode, String message) {
        return fail(resultCode.getCode(), message);
    }

    public static <T> Result<T> fail(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }

    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }

    // ---------- getter / setter ----------

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Result{code=" + code + ", message='" + message + "', traceId='" + traceId + "'}";
    }
}
