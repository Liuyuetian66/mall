package com.mall.common.api;

import com.mall.common.web.TraceContext;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应结构。所有 HTTP 接口都必须返回该结构。
 *
 * <p>设计约定：
 * <ul>
 *     <li>业务失败（库存不足、订单状态非法等）返回 HTTP 200 + 业务码，
 *         前端只判断 {@code code}，不要用 HTTP 状态码区分业务成败</li>
 *     <li>参数错误、未登录、系统异常才使用 4xx / 5xx，由
 *         {@code GlobalExceptionHandler} 统一转换</li>
 *     <li>{@code traceId} 由 TraceIdFilter 写入 MDC，这里自动带出，
 *         用户报障时只需提供该串即可定位全链路日志</li>
 * </ul>
 *
 * <p>响应示例（注意 {@code success} 字段由 {@link #isSuccess()} 序列化生成）：
 * <pre>
 * {
 *   "code": 0,
 *   "message": "成功",
 *   "data": {...},
 *   "traceId": "18f3a1b2c3d_9a1f2b3c4d5e6f70",
 *   "timestamp": 1729871234567,
 *   "success": true
 * }
 * </pre>
 *
 * @param <T> 业务数据类型
 */
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 业务状态码，0 表示成功，其余见 {@link ResultCode} */
    private int code;

    /** 提示信息，可直接展示给用户 */
    private String message;

    /** 业务数据，失败时为 null */
    private T data;

    /** 链路追踪 ID，排查问题时交给后端即可 */
    private String traceId;

    /** 服务器时间戳（毫秒），便于排查客户端与服务端时间不一致问题 */
    private long timestamp;

    /**
     * 无参构造。创建时自动抓取当前线程的 traceId 与服务器时间，
     * 保证响应里永远带得上排查信息，业务代码不需要手动设置。
     */
    public Result() {
        this.traceId = TraceContext.getTraceId();
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应，无返回数据。
     * <p>适用于删除、状态变更等只关心成功与否的接口。
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * 成功响应，携带业务数据。
     *
     * @param data 业务数据，允许为 null
     * @param <T>  业务数据类型
     */
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.code = ResultCode.SUCCESS.getCode();
        result.message = ResultCode.SUCCESS.getMessage();
        result.data = data;
        return result;
    }

    /**
     * 失败响应，使用错误码枚举里的默认提示文案。
     *
     * @param resultCode 预定义错误码
     */
    public static <T> Result<T> fail(ResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 失败响应，覆盖默认提示文案（需要拼接动态内容时使用）。
     *
     * @param resultCode 预定义错误码
     * @param message    自定义提示，例如「商品库存不足，仅剩 2 件」
     */
    public static <T> Result<T> fail(ResultCode resultCode, String message) {
        return fail(resultCode.getCode(), message);
    }

    /**
     * 失败响应，直接指定错误码与文案。
     * <p>一般不建议业务代码直接调用，优先走 {@link ResultCode} 与
     * {@code BizException}，避免错误码散落各处。
     *
     * @param code    业务状态码
     * @param message 提示信息
     */
    public static <T> Result<T> fail(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }

    /**
     * 是否成功。序列化成 JSON 时会额外产生一个 {@code success} 字段，
     * 前端可直接使用，等价于 {@code code == 0}。
     */
    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }

    // ---------------------------------------------------------------- getter / setter

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

    /** 只输出关键信息，避免打印大对象导致日志膨胀 */
    @Override
    public String toString() {
        return "Result{code=" + code + ", message='" + message + "', traceId='" + traceId + "'}";
    }
}
